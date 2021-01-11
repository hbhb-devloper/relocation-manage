package com.hbhb.cw.relocation.service.impl;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.Category;
import com.hbhb.cw.relocation.enums.InvoiceErrorCode;
import com.hbhb.cw.relocation.enums.IsReceived;
import com.hbhb.cw.relocation.enums.PaymentType;
import com.hbhb.cw.relocation.enums.RelocationErrorCode;
import com.hbhb.cw.relocation.enums.UnitAbbr;
import com.hbhb.cw.relocation.exception.InvoiceException;
import com.hbhb.cw.relocation.exception.RelocationException;
import com.hbhb.cw.relocation.mapper.IncomeDetailMapper;
import com.hbhb.cw.relocation.mapper.IncomeMapper;
import com.hbhb.cw.relocation.mapper.ProjectMapper;
import com.hbhb.cw.relocation.model.RelocationIncome;
import com.hbhb.cw.relocation.model.RelocationIncomeDetail;
import com.hbhb.cw.relocation.model.RelocationProject;
import com.hbhb.cw.relocation.rpc.DictApiExp;
import com.hbhb.cw.relocation.rpc.UnitApiExp;
import com.hbhb.cw.relocation.rpc.UserApiExp;
import com.hbhb.cw.relocation.service.IncomeService;
import com.hbhb.cw.relocation.util.BigDecimalUtil;
import com.hbhb.cw.relocation.web.vo.AmountVO;
import com.hbhb.cw.relocation.web.vo.IncomeExportVO;
import com.hbhb.cw.relocation.web.vo.IncomeImportVO;
import com.hbhb.cw.relocation.web.vo.IncomeReqVO;
import com.hbhb.cw.relocation.web.vo.IncomeResVO;
import com.hbhb.cw.relocation.web.vo.ProjectSelectVO;
import com.hbhb.cw.systemcenter.enums.DictCode;
import com.hbhb.cw.systemcenter.enums.TypeCode;
import com.hbhb.cw.systemcenter.model.Unit;
import com.hbhb.cw.systemcenter.vo.DictVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author hyk
 * @since 2020-09-28
 */
@Service
@Slf4j
@SuppressWarnings(value = {"unchecked"})
public class IncomeServiceImpl implements IncomeService {

    @Resource
    private IncomeMapper incomeMapper;
    @Resource
    private IncomeDetailMapper incomeDetailMapper;
    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private UnitApiExp unitApiExp;
    @Resource
    private UserApiExp userApi;
    @Resource
    private DictApiExp dictApi;
    @Resource
    private UnitApiExp unitApi;

    private final List<String> msg = new CopyOnWriteArrayList<>();

    @Override
    public PageResult<IncomeResVO> getIncomeList(Integer pageNum, Integer pageSize, IncomeReqVO cond, Integer userId) {


        Map<Integer, String> unitMap = unitApiExp.getUnitMapById();
        UserInfo user = userApi.getUserInfoById(userId);

        if (userApi.isAdmin(userId) && cond.getUnitId() == null) {
            cond.setUnitId(user.getUnitId());
        }
        Unit unitInfo = unitApi.getUnitInfo(user.getUnitId());
        if (UnitAbbr.CWB.value().equals(unitInfo.getUnitName())
                || UnitAbbr.WLB.value().equals(unitInfo.getUnitName())) {
            cond.setUnitId(null);
        }
        PageRequest<IncomeResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<IncomeResVO> incomeList = incomeMapper.getIncomeList(cond, request);
        List<IncomeResVO> list = incomeList.getList();
        Map<String, String> typeMap = getInvoiceTypeByValue();
        Map<String, String> categoryMap = getCategory();
        Map<String, String> paymentMap = getPaymentMap();
        Map<Integer, String> isReceivedMap = getIsReceived();

        for (IncomeResVO incomeResVO : list) {
            String category = incomeResVO.getCategory();
            // 类型
            incomeResVO.setCategory(categoryMap.get(category));
            //  拼装收款发票类型
            incomeResVO.setInvoiceType(typeMap.get(incomeResVO.getInvoiceTypeLabel()));
            BigDecimal monthAmount = incomeMapper.getMonthAmount(incomeResVO.getId(), DateUtil.getCurrentMonth());
            incomeResVO.setMonthAmount(monthAmount);
            incomeResVO.setUnit(unitMap.get(Integer.valueOf(incomeResVO.getUnit())));
            // 回款状态
            incomeResVO.setIsReceived(isReceivedMap.get(parseInt(incomeResVO.getIsReceived())));
            // 收款类型
            incomeResVO.setPaymentType(paymentMap.get(incomeResVO.getPaymentType()));
        }
        return incomeList;
    }

    @Override
    public List<RelocationIncomeDetail> getIncomeDetail(Long id, Integer isNeed) {
        String currentMonth = DateUtil.getCurrentMonth();
        return incomeMapper.selectDetailById(id, isNeed, currentMonth);
    }

    @Override
    public void updateIncomeDetail(RelocationIncomeDetail detail) {
        incomeDetailMapper.updateById(detail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addIncomeDetail(RelocationIncomeDetail detail, Integer userId) {

        UserInfo user = userApi.getUserInfoById(userId);

        // 收款月份
        detail.setPayMonth(DateUtil.getCurrentMonth());
        detail.setPayMonth(detail.getPayMonth().replace("-", ""));

        // 收款人
        detail.setPayee(user.getNickName());

        // 创建时间
        detail.setCreateTime(DateUtil.getCurrentDate());
        incomeDetailMapper.insert(detail);
        //未收减少
        RelocationIncome single = incomeMapper.single(detail.getIncomeId());

        // 更新收款信息
        RelocationIncome income = new RelocationIncome();
        income.setId(detail.getIncomeId());

        // 未收
        BigDecimal subtractRe = single.getUnreceived().subtract(detail.getAmount());
        income.setUnreceived(subtractRe);
        if (subtractRe.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvoiceException(InvoiceErrorCode.RELOCATION_INCOME_AMOUNT_ERROR);
        }
        //已收增加
        income.setReceived(single.getReceived().add(detail.getAmount()));
        incomeMapper.updateTemplateById(income);

        // 判断更新后的收款状态
        RelocationIncome income1 = incomeMapper.single(detail.getIncomeId());
        BigDecimal receivable = income1.getReceivable();
        BigDecimal unreceived = income1.getUnreceived();
        BigDecimal received = income1.getReceived();

        //已收完的情况
        if (received.compareTo(receivable) == 0 && unreceived.compareTo(BigDecimal.ZERO) == 0) {
            income1.setIsReceived(IsReceived.RECEIVED.key());
            incomeMapper.updateTemplateById(income1);
        }

        //部分回款
        if (received.compareTo(receivable) < 0 && single.getUnreceived().compareTo(BigDecimal.ZERO) > 0) {
            income1.setIsReceived(IsReceived.PART_RECEIVED.key());
            incomeMapper.updateTemplateById(income1);
        }

        // 更新基础信息表预付款到账金额，决算款到账金额
        List<String> contractNum = new ArrayList<>();
        contractNum.add(income.getContractNum());
        List<RelocationProject> projectList = new ArrayList<>();

        // 获取收款项合同对应所有项目
        List<AmountVO> amountVO = projectMapper.selectCompensationAmount(contractNum);

        // 合同施工费总额
        List<ProjectSelectVO> totalList = projectMapper.selectSumConstructionBudget(contractNum);
        Map<String, BigDecimal> constructionBudgetMap = totalList.stream().collect(Collectors.
                toMap(ProjectSelectVO::getNum, ProjectSelectVO::getConstructionBudget));

        // 1.按合同编号找出预付款到账金额 并按施工费预算比例修改预付款到账金额
        if (single.getPaymentType().equals(PaymentType.ADVANCE_PAYMENT.key())) {
            for (AmountVO amount : amountVO) {
                RelocationProject project = new RelocationProject();
                // 预付款到账金额 = 施工费占整个合同比例*本次收款到账金额
                project.setAnticipatePayable(amount.getAnticipatePayable()
                        .add(detail.getAmount().multiply(amount.getConstructionBudget()
                                .divide(constructionBudgetMap
                                        .get(amount.getContractNum()), 4, 4))));
                project.setId(amount.getId());
                projectList.add(project);
            }
            projectMapper.updateBatchTempById(projectList);
        }

        //2.  按合同编号找出决算款到账金额 并按施工费预算比例修决算款到账金额
        if (single.getPaymentType().equals(PaymentType.FINAL_PARAGRAPH.key())) {
            for (AmountVO amount : amountVO) {
                RelocationProject project = new RelocationProject();
                // 预付款到账金额 = 施工费占整个合同比例*本次收款到账金额
                project.setFinalPayment(amount.getAnticipatePayable()
                        .add(detail.getAmount().multiply(amount.getConstructionBudget()
                                .divide(constructionBudgetMap
                                        .get(amount.getContractNum()), 4, 4))));
                project.setId(amount.getId());
                projectList.add(project);
            }
            projectMapper.updateBatchTempById(projectList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void addSaveRelocationInvoice(List<IncomeImportVO> dataList, Map<Integer, String> importHeadMap) {
        List<String> invoiceNum = incomeMapper.selectInvoiceNum();
        // 对比导入表头与模板表头若相同则执行后续操作，若不同则抛出异常
        Map<Integer, String> headMap = projectHead();
        for (Map.Entry<Integer, String> entry : headMap.entrySet()) {
            String m1value = entry.getValue() == null ? "" : entry.getValue();
            String m2value = importHeadMap.get(entry.getKey()) == null ? "" : importHeadMap.get(entry.getKey());
            if (!m1value.equals(m2value)) {
                //若两个map中相同key对应的value不相等
                throw new RelocationException(RelocationErrorCode.RELOCATION_TEMPLATE_ERROR);
            }
        }
        // 转换单位
        Map<String, Integer> unitMap = unitApiExp.getUnitMapByUnitName();
        Map<String, String> invoiceTypeMap = getInvoiceTypeByLabel();
        List<String> error = new ArrayList<>();
        // 收款
        List<RelocationIncome> incomeList = new ArrayList<>();
        int i = 3;
        for (IncomeImportVO importVO : dataList) {
            RelocationIncome income = new RelocationIncome();
            // 1 迁改 2 搬迁 3 代建
            income.setCategory("迁改".equals(importVO.getCategory()) ? 1 : "搬迁".equals(importVO.getCategory()) ? 2 : 3);
            income.setUnitId(unitMap.get(importVO.getUnit()) == null ? 11 : unitMap.get(importVO.getUnit()));
            income.setSupplier(importVO.getSupplier());
            income.setContractNum(importVO.getContractNum());
            income.setContractName(importVO.getContractName());
            income.setStartTime(DateUtil.string3DateYMD(importVO.getStartTime()));
            income.setContractDeadline(DateUtil.string3DateYMD(importVO.getContractDeadline()));
            income.setContractAmount(BigDecimalUtil.getBigDecimal(importVO.getContractAmount()));
            income.setInvoiceTime(DateUtil.string3DateYMD(importVO.getInvoiceTime()));
            if (invoiceNum.contains(importVO.getInvoiceNum())) {
                error.add("excel第：" + i + "行数据已存在，请检查导入数据是否正确");
            }
            income.setInvoiceNum(importVO.getInvoiceNum());
            income.setInvoiceType(isEmpty(importVO.getInvoiceType()) ?
                    0 : Integer.parseInt(invoiceTypeMap.get(importVO.getInvoiceType())));
            income.setAmount(BigDecimalUtil.getBigDecimal(importVO.getAmount()));
            income.setTax(BigDecimalUtil.getBigDecimal(importVO.getTax()));
            income.setTaxIncludeAmount(BigDecimalUtil.getBigDecimal(importVO.getTaxIncludeAmount()));
            income.setConstructionName(importVO.getConstructionName());
            // 1 预付款   2 决算款
            income.setPaymentType(PaymentType.ADVANCE_PAYMENT.value().equals(importVO.getPaymentType()) ? 1 : 2);
            // 10-已收款 20-未收款 30-部分回款
            income.setIsReceived(IsReceived.NOT_RECEIVED.value().equals(importVO.getCategory()) ? IsReceived.NOT_RECEIVED.key()
                    : IsReceived.PART_RECEIVED.value().equals(importVO.getCategory()) ? IsReceived.PART_RECEIVED.key() : IsReceived.RECEIVED.key());
            income.setAging(importVO.getAging());
            income.setReceivable(BigDecimalUtil.getBigDecimal(importVO.getReceivable()));
            income.setReceived(BigDecimalUtil.getBigDecimal(importVO.getReceived()));
            income.setUnreceived(BigDecimalUtil.getBigDecimal(importVO.getUnreceived()));
            income.setAging(importVO.getAging());
            income.setReceivable(BigDecimalUtil.getBigDecimal(importVO.getReceivable()));
            income.setReceived(BigDecimalUtil.getBigDecimal(importVO.getReceived()));
            income.setUnreceived(BigDecimalUtil.getBigDecimal(importVO.getUnreceived()));
            //插入收款信息
            incomeList.add(income);
            i++;
        }
        msg.clear();
        msg.addAll(error);
        if (error.size() == 0) {
            incomeMapper.insertBatch(incomeList);
            List<RelocationIncomeDetail> details = new ArrayList<>();
            for (RelocationIncome income : incomeList) {
                if (!isEmpty(income.getReceiptNum())) {
                    RelocationIncomeDetail incomeDetail = RelocationIncomeDetail.builder()
                            .incomeId(income.getId())
                            .createTime(DateUtil.getCurrentDate())
                            .payee(income.getPayee())
                            .amount(income.getAmount())
                            .payMonth(DateUtil.getCurrentMonth())
                            .build();
                    details.add(incomeDetail);
                }
            }
            //插入收款下的收款详情
            incomeDetailMapper.insertBatch(details);
        }
    }

    @Override
    public List<IncomeExportVO> selectExportListByCondition(IncomeReqVO cond, Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        if (userApi.isAdmin(userId) && cond.getUnitId() == null) {
            cond.setUnitId(user.getUnitId());
        }

        Unit unitInfo = unitApi.getUnitInfo(user.getUnitId());
        if (UnitAbbr.CWB.value().equals(unitInfo.getUnitName())
                || UnitAbbr.WLB.value().equals(unitInfo.getUnitName())) {
            cond.setUnitId(null);
        }
        List<IncomeExportVO> relocationIncomeExport = incomeMapper.selectExportList(cond);
        // 类型
        Map<Integer, String> unitMap = unitApiExp.getUnitMapById();
        Map<String, String> categoryMap = getCategory();
        Map<Integer, String> isReceivedMap = getIsReceived();
        int i = 1;
        for (IncomeExportVO export : relocationIncomeExport) {
            String category = export.getCategory();
            export.setCategory(categoryMap.get(category));
            // 回款状态
            export.setIsReceived(isReceivedMap.get(parseInt(export.getIsReceived())));
            export.setNum(i);
            export.setUnit(unitMap.get(Integer.parseInt(export.getUnit())));
            i++;
        }
        return relocationIncomeExport;
    }


    private Map<String, String> getInvoiceTypeByLabel() {
        List<DictVO> compensationSateList = dictApi.getDict(TypeCode.RELOCATION.value(), DictCode.RELOCATION_INVOICE_TYPE.value());
        return compensationSateList.stream().collect(Collectors.toMap(DictVO::getLabel, DictVO::getValue));
    }

    private Map<String, String> getInvoiceTypeByValue() {
        List<DictVO> compensationSateList = dictApi.getDict(TypeCode.RELOCATION.value(), DictCode.RELOCATION_INVOICE_TYPE.value());
        return compensationSateList.stream().collect(Collectors.toMap(DictVO::getValue, DictVO::getLabel));
    }

    private Map<String, String> getCategory() {
        // 发票类型
        Map<String, String> categoryMap = new HashMap<>(100);
        categoryMap.put(Category.RELOCATION.key().toString(), Category.RELOCATION.value());
        categoryMap.put(Category.REMOVAL.key().toString(), Category.REMOVAL.value());
        categoryMap.put(Category.CONSTRUCTION.key().toString(), Category.CONSTRUCTION.value());
        return categoryMap;
    }

    private Map<Integer, String> getIsReceived() {
        // 收款状态
        Map<Integer, String> statusMap = new HashMap<>(100);
        statusMap.put(IsReceived.RECEIVED.key(), IsReceived.RECEIVED.value());
        statusMap.put(IsReceived.NOT_RECEIVED.key(), IsReceived.NOT_RECEIVED.value());
        statusMap.put(IsReceived.PART_RECEIVED.key(), IsReceived.PART_RECEIVED.value());
        return statusMap;
    }

    private Map<String, String> getPaymentMap() {
        // 款项类型
        Map<String, String> paymentMap = new HashMap<>(100);
        paymentMap.put(PaymentType.ADVANCE_PAYMENT.key().toString(), PaymentType.ADVANCE_PAYMENT.value());
        paymentMap.put(PaymentType.FINAL_PARAGRAPH.key().toString(), PaymentType.FINAL_PARAGRAPH.value());
        paymentMap.put(PaymentType.FINAL_PAYMENT.key().toString(), PaymentType.FINAL_PAYMENT.value());
        return paymentMap;
    }

    @Override
    public void judgeFileName(String fileName) {
        int i = fileName.lastIndexOf(".");
        String name = fileName.substring(i);
        if (!(ExcelTypeEnum.XLS.getValue().equals(name) || ExcelTypeEnum.XLSX.getValue().equals(name))) {
            throw new RelocationException(RelocationErrorCode.FILE_DATA_NAME_ERROR);
        }
    }

    private Map<Integer, String> projectHead() {
        Map<Integer, String> headMap = new HashMap<>(100);
        headMap.put(0, "序号");
        headMap.put(1, "类别");
        headMap.put(2, "经办单位");
        headMap.put(3, "供应商");
        headMap.put(4, "合同编号");
        headMap.put(5, "合同名称");
        headMap.put(6, "起始时间");
        headMap.put(7, "合截止时间");
        headMap.put(8, "合同金额");
        headMap.put(9, "开票日期");
        headMap.put(10, "发票号码");
        headMap.put(11, "发票类型");
        headMap.put(12, "价款");
        headMap.put(13, "税额");
        headMap.put(14, "价税合计");
        headMap.put(15, "工程名");
        headMap.put(16, "收款情况");
        headMap.put(17, "账龄分类");
        headMap.put(18, "账龄（月）");
        headMap.put(19, "应收");
        headMap.put(20, "已收");
        headMap.put(21, "未收");
        headMap.put(22, "收款类型");
        headMap.put(23, "当月收款金额");
        headMap.put(24, "收款单号");
        headMap.put(25, "收款人");


        return headMap;
    }

    @Override
    public List<String> getMsg() {
        return this.msg;
    }
}
