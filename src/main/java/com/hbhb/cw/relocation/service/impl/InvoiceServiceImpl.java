package com.hbhb.cw.relocation.service.impl;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.*;
import com.hbhb.cw.relocation.exception.InvoiceException;
import com.hbhb.cw.relocation.exception.RelocationException;
import com.hbhb.cw.relocation.mapper.IncomeMapper;
import com.hbhb.cw.relocation.mapper.InvoiceMapper;
import com.hbhb.cw.relocation.mapper.ProjectMapper;
import com.hbhb.cw.relocation.model.RelocationIncome;
import com.hbhb.cw.relocation.model.RelocationInvoice;
import com.hbhb.cw.relocation.model.RelocationProject;
import com.hbhb.cw.relocation.rpc.DictApiExp;
import com.hbhb.cw.relocation.rpc.UnitApiExp;
import com.hbhb.cw.relocation.rpc.UserApiExp;
import com.hbhb.cw.relocation.service.InvoiceService;
import com.hbhb.cw.relocation.util.BigDecimalUtil;
import com.hbhb.cw.relocation.web.vo.*;
import com.hbhb.cw.systemcenter.enums.DictCode;
import com.hbhb.cw.systemcenter.enums.TypeCode;
import com.hbhb.cw.systemcenter.enums.UnitEnum;
import com.hbhb.cw.systemcenter.vo.DictVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.alibaba.excel.util.StringUtils.isEmpty;
import static java.lang.Integer.parseInt;

/**
 * @author xiaokang
 * @since 2020-09-22
 */
@Service
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    @Resource
    private InvoiceMapper invoiceMapper;

    @Resource
    private UserApiExp userApi;

    @Resource
    private UnitApiExp unitApi;

    @Resource
    private IncomeMapper incomeMapper;

    @Resource
    private DictApiExp dictApi;

    @Resource
    private ProjectMapper projectMapper;

    @Override
    public PageResult<InvoiceResVO> getInvoiceList(Integer pageNum, Integer pageSize,
                                                   InvoiceReqVO cond, Integer userId) {

        List<Integer> unitIds = new ArrayList<>();

        if (UnitEnum.isBenbu(cond.getUnitId())) {
            unitIds = unitApi.getSubUnit(cond.getUnitId());
        }
        cond.setUnitIds(unitIds);
        PageRequest<InvoiceResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<InvoiceResVO> invoiceResVo = invoiceMapper.selectListByCondition(cond, request);
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        // 获取发票类型字典
        List<DictVO> type = dictApi.getDict(TypeCode.RELOCATION.value(), DictCode.RELOCATION_INVOICE_TYPE.value());
        Map<String, String> typeMap = type.stream().collect(Collectors.toMap(DictVO::getValue, DictVO::getLabel));
        // 获取回款状态类型
        Map<Integer, String> statusMap = getPaymentStatus();
        invoiceResVo.getList().forEach(item -> {
            // 转换发票类型
            item.setInvoiceType(typeMap.get(item.getInvoiceType()));
            // 是否为自定义开票
            item.setIsImport(State.ONE.value().equals(item.getState()) ? State.YES.value() : State.NO.value());
            // 转换单位
            item.setUnit(unitMap.get(item.getUnitId()));
            // 转换区域
            item.setDistrict(unitMap.get(item.getDistrictId()));
            // 收款状态
            item.setIsReceived(statusMap.get(parseInt(item.getIsReceived())));
            // 发票状态
            item.setState(State.ONE.value().equals(item.getState()) ? InvoiceState.BLUE_STATE.value() : InvoiceState.RED_STATE.value());
        });
        return invoiceResVo;
    }

    @Override
    public RelocationInvoice getInvoiceDetail(Long id) {
        return invoiceMapper.single(id);
    }

    @Override
    public void updateInvoice(InvoiceResVO invoiceVo) {
        RelocationInvoice invoiceInfo = invoiceMapper.single(invoiceVo.getId());
        RelocationInvoice invoice = translation(invoiceVo);
        RelocationProject project = projectMapper.single(invoice.getProjectId());
        RelocationIncome income = getRelocationIncome(invoice, project);
        invoiceMapper.updateById(invoice);
        RelocationIncome single = incomeMapper.createLambdaQuery()
                .andEq(RelocationIncome::getInvoiceNum, invoiceInfo.getInvoiceNumber())
                .single();
        income.setId(single.getId());
        incomeMapper.updateById(income);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addInvoice(InvoiceResVO invoiceVo) {
        Integer re = invoiceMapper.selectListByNumber(invoiceVo.getInvoiceNumber());
        if (re > 0) {
            throw new InvoiceException(InvoiceErrorCode.RELOCATION_INCOME_NUMBER_EXIST);
        }
        // 处理发票内容
        RelocationInvoice invoice = translation(invoiceVo);
        invoiceMapper.insert(invoice);
        // 处理收款数据内容
        RelocationProject project = projectMapper.single(invoice.getProjectId());
        RelocationIncome relocationIncome = getRelocationIncome(invoice, project);
        incomeMapper.insert(relocationIncome);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSaveRelocationInvoice(List<InvoiceImportVO> dataList) {
        // 查询所有发票编号用于与现有发票做对比
        List<String> invoiceNumber = invoiceMapper.selectInvoiceNumber();
        // 转换单位
        Map<String, Integer> unitMap = unitApi.getUnitMapByUnitName();
        // 获取发票类型字典
        List<DictVO> type = dictApi.getDict(TypeCode.RELOCATION.value(), DictCode.RELOCATION_INVOICE_TYPE.value());
        Map<String, String> typeMap = type.stream().collect(Collectors.toMap(DictVO::getLabel, DictVO::getValue));
        // 发票
        List<RelocationInvoice> invoiceList = new ArrayList<>();
        // 收款
        List<RelocationIncome> incomeList = new ArrayList<>();
        long count = dataList.stream().distinct().count();
        // 文件内查重
        if (count < dataList.size()) {
            throw new InvoiceException(InvoiceErrorCode.RELOCATION_INCOME_EXCEL_EXIST);
        }
        List<String> msg = new ArrayList<>();
        int i = 3;
        for (InvoiceImportVO invoiceImport : dataList) {
            RelocationInvoice invoice = new RelocationInvoice();
            BeanUtils.copyProperties(invoiceImport, invoice);
            if (invoiceNumber.contains(invoiceImport.getInvoiceNumber())) {
                msg.add("在excel表中第" + i + "行，发票号码为:" + invoiceImport.getInvoiceNumber() + "已存在发票表中,请仔细检查后重新导入");
            }
            // 备注列信息
            String remake = invoice.getRemake();
            // 按照英文分隔符划分
            remake = remake.replace("；", ";");
            List<String> arrList = Arrays.asList(remake.split(";"));
            if (arrList.size() != 4) {
                msg.add("请检查excel第" + i + "行，备注修改列：" + remake + "格式");
            }
            if (msg.size() != 0) {
                throw new RelocationException(RelocationErrorCode.RELOCATION_IMPORT_DATE_ERROR, msg.toString());
            }
            List<ProjectReqVO> projectRes = getProjectResVo(remake, unitMap);
            if (projectRes.size() == 1) {
                projectRes.forEach(item -> invoice.setProjectId(item.getId()));
            }
            // 判断是否有对应项目
            if (!isEmpty(invoice.getProjectId())) {
                RelocationProject project = projectMapper.single(invoice.getProjectId());
                // 地区默认11
                invoice.setDistrict(11);
                // 经办单位
                invoice.setUnitId(unitMap.get(invoiceImport.getUnitId()));
                // 发票编号
                invoice.setInvoiceNumber(invoiceImport.getInvoiceNumber());
                String invoiceType = invoiceImport.getInvoiceType();
                invoice.setInvoiceType(Integer.valueOf(typeMap.get(invoiceType)));
                // 开票日期格式转换 yyyy/MM/dd
                invoice.setInvoiceTime(DateUtil.string2DateYMD(invoiceImport.getInvoiceTime()));
                // 金额
                invoice.setAmount(BigDecimalUtil.getBigDecimal(invoiceImport.getAmount()));
                // 税率 空
                invoice.setTaxRate(new BigDecimal("0"));
                // 税额
                invoice.setTaxAmount(BigDecimalUtil.getBigDecimal(invoiceImport.getTaxAmount()));
                // 价税合计
                invoice.setTaxIncludeAmount(BigDecimalUtil.getBigDecimal(invoiceImport.getTaxIncludeAmount()));
                // 备注
                invoice.setRemake(invoiceImport.getRemake());
                // 收款负责人/申请人
                invoice.setApplicant(invoiceImport.getApplicant());
                // 收款
                RelocationIncome income = getRelocationIncome(invoice, project);
                invoiceList.add(invoice);
                incomeList.add(income);

            }
            i++;
        }
        // 批量插入发票、收款信息
        incomeMapper.insertBatch(incomeList);
        invoiceMapper.insertBatch(invoiceList);
    }

    @Override
    public List<InvoiceExportResVO> selectExportListByCondition(InvoiceReqVO vo, Integer userId) {
        setConditionDetail(vo, userId);
        List<InvoiceExportResVO> exportResVos = invoiceMapper.selectExportListByCondition(vo);
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        // 组装发票类型 发票状态，单位，区域
        int i = 1;
        for (InvoiceExportResVO export : exportResVos) {
            // 序号
            export.setNum(i);
            // 发票类型
            export.setInvoiceType(State.ONE.value().equals(export.getInvoiceType()) ? InvoiceType.PLAIN_INVOICE.value() : InvoiceType.SPECIAL_INVOICE.value());
            // 发票状态
            export.setState(State.ONE.value().equals(export.getState()) ? InvoiceState.BLUE_STATE.value() : InvoiceState.RED_STATE.value());
            // 是否自定义开票
            export.setIsImport(State.ONE.value().equals(export.getState()) ? State.YES.value() : State.NO.value());
            // 单位
            export.setUnit(unitMap.get(export.getUnitId()));
            // 区县
            export.setDistrict(unitMap.get(export.getDistrictId()));
            i++;
        }
        return exportResVos;
    }

    @Override
    public RelocationInvoice getInvoice(String invoiceNum) {
        return invoiceMapper.selectInvoiceByInvoiceNum(invoiceNum);
    }


    private void setConditionDetail(InvoiceReqVO cond, Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        if (userApi.isAdmin(userId) && cond.getUnitId() == null) {
            cond.setUnitId(user.getUnitId());
        }
        if (!StringUtils.isEmpty(cond.getInvoiceTimeFrom())) {
            cond.setInvoiceTimeFrom(cond.getInvoiceTimeFrom() + " 00:00:00");
        }
        if (!StringUtils.isEmpty(cond.getInvoiceTimeTo())) {
            cond.setInvoiceTimeTo(cond.getInvoiceTimeTo() + " 23:59:59");
        }
    }

    private RelocationInvoice translation(InvoiceResVO invoiceVo) {
        RelocationInvoice invoice = new RelocationInvoice();
        // 单位
        Map<String, Integer> unitMap = unitApi.getUnitMapByUnitName();
        // 判断是否与基础信息表数据对应
        List<ProjectReqVO> projectRes = getProjectResVo(invoiceVo.getRemake(), unitMap);
        if (projectRes.size() > 1) {
            throw new RelocationException(RelocationErrorCode.RELOCATION_INVOICE_REMAKE_ERROR);
        } else if (projectRes.size() == 0) {
            throw new InvoiceException(InvoiceErrorCode.RELOCATION_INVOICE_REMAKE_ERROR);
        } else {
            projectRes.forEach(item -> invoice.setProjectId(item.getId()));
        }
        BeanUtils.copyProperties(invoiceVo, invoice);
        invoice.setInvoiceTime(DateUtil.string2DateYMD(invoiceVo.getInvoiceTime()));
        invoice.setState(Integer.valueOf(invoiceVo.getState()));
        invoice.setIsImport(Integer.valueOf(invoiceVo.getIsImport()));
        invoice.setDistrict(Integer.valueOf(invoiceVo.getDistrict()));
        invoice.setInvoiceType(Integer.valueOf(invoiceVo.getInvoiceType()));
        return invoice;
    }

    private RelocationIncome getRelocationIncome(RelocationInvoice invoice, RelocationProject project) {
        RelocationIncome income = new RelocationIncome();
        // 发票合同信息获取
        income.setContractNum(project.getContractNum());
        income.setContractName(project.getContractName());
        income.setStartTime(project.getPlanStartTime());
        income.setContractDeadline(project.getPlanEndTime());
        income.setContractAmount(project.getCompensationAmount());
        income.setConstructionName(project.getProjectName());
        //类别 默认迁改
        income.setCategory(1);
        income.setUnitId(invoice.getUnitId());
        income.setSupplier(invoice.getBuyerName());
        income.setInvoiceTime(invoice.getInvoiceTime());
        income.setInvoiceNum(invoice.getInvoiceNumber());
        income.setInvoiceType(invoice.getInvoiceType());
        income.setAmount(invoice.getAmount());
        income.setTax(invoice.getTaxAmount());
        income.setTaxIncludeAmount(invoice.getTaxIncludeAmount());
        // 回款状态默认新增时默认未回款
        income.setIsReceived(IsReceived.NOT_RECEIVED.key());
        // 应收
        income.setReceivable(invoice.getAmount());
        // 已收
        income.setReceived(BigDecimal.valueOf(0));
        // 未收
        income.setUnreceived(invoice.getAmount());
        // 收款人
        return income;
    }


    private List<ProjectReqVO> getProjectResVo(String remake, Map<String, Integer> unitMap) {
        remake = remake.replace("；", ";");
        // 按照英文分隔符划分
        List<String> arrList = Arrays.asList(remake.split(";"));
        // 判断备注列数据是否对应基础信息
        // 1-合同编号
        String contractNum = arrList.get(0);
        // 2-区县
        String unitName = arrList.get(1);
        // 3-项目名称
        String projectName = arrList.get(3);
        Integer unitId = unitMap.get(unitName);
        ProjectReqVO projectVo = new ProjectReqVO();
        projectVo.setUnitId(unitId);
        projectVo.setProjectName(projectName);
        projectVo.setContractNum(contractNum);
        //  通过备注修改列对比基础项目信息表
        return projectMapper.selectProjectByCondList(projectVo);
    }

    private Map<Integer, String> getPaymentStatus() {
        // 收款状态
        Map<Integer, String> statusMap = new HashMap<>(100);
        statusMap.put(IsReceived.RECEIVED.key(), IsReceived.RECEIVED.value());
        statusMap.put(IsReceived.NOT_RECEIVED.key(), IsReceived.NOT_RECEIVED.value());
        statusMap.put(IsReceived.PART_RECEIVED.key(), IsReceived.PART_RECEIVED.value());
        return statusMap;
    }


    @Override
    public void judgeFileName(String fileName) {
        int i = fileName.lastIndexOf(".");
        String name = fileName.substring(i);
        if (!(ExcelTypeEnum.XLS.getValue().equals(name) || ExcelTypeEnum.XLSX.getValue()
                .equals(name))) {
            throw new RelocationException(RelocationErrorCode.FILE_DATA_NAME_ERROR);
        }
    }
}
