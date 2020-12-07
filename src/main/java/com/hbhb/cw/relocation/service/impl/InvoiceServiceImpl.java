package com.hbhb.cw.relocation.service.impl;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.*;
import com.hbhb.cw.relocation.exception.InvoiceException;
import com.hbhb.cw.relocation.exception.RelocationException;
import com.hbhb.cw.relocation.mapper.IncomeDetailMapper;
import com.hbhb.cw.relocation.mapper.IncomeMapper;
import com.hbhb.cw.relocation.mapper.InvoiceMapper;
import com.hbhb.cw.relocation.mapper.ProjectMapper;
import com.hbhb.cw.relocation.model.RelocationIncome;
import com.hbhb.cw.relocation.model.RelocationIncomeDetail;
import com.hbhb.cw.relocation.model.RelocationInvoice;
import com.hbhb.cw.relocation.rpc.DictApiExp;
import com.hbhb.cw.relocation.rpc.UnitApiExp;
import com.hbhb.cw.relocation.rpc.UserApiExp;
import com.hbhb.cw.relocation.service.InvoiceService;
import com.hbhb.cw.relocation.web.vo.InvoiceExportResVO;
import com.hbhb.cw.relocation.web.vo.InvoiceImportVO;
import com.hbhb.cw.relocation.web.vo.InvoiceReqVO;
import com.hbhb.cw.relocation.web.vo.InvoiceResVO;
import com.hbhb.cw.systemcenter.enums.DictCode;
import com.hbhb.cw.systemcenter.enums.TypeCode;
import com.hbhb.cw.systemcenter.vo.DictVO;
import com.hbhb.cw.systemcenter.vo.UnitTopVO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private IncomeDetailMapper detailMapper;

    @Resource
    private ProjectMapper projectMapper;

    @Override
    public PageResult<InvoiceResVO> getInvoiceList(Integer pageNum, Integer pageSize,
                                                   InvoiceReqVO cond, Integer userId) {
        UnitTopVO parentUnit = unitApi.getTopUnit();
        List<Integer> unitIds = new ArrayList<>();

        if (parentUnit.getBenbu().equals(cond.getUnitId())) {
            unitIds = unitApi.getSubUnit(parentUnit.getBenbu());
        }
        cond.setUnitIds(unitIds);
        PageRequest<InvoiceResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<InvoiceResVO> invoiceResVo = invoiceMapper.selectListByCondition(cond, request);
        Map<Integer, String> unitMap = unitApi.getUnitMapById();

        // 获取发票类型字典
        List<DictVO> type = dictApi.getDict(TypeCode.RELOCATION.value(), DictCode.RELOCATION_INVOICE_TYPE.value());
        Map<String, String> typeMap = type.stream().collect(Collectors.toMap(DictVO::getValue, DictVO::getLabel));
        invoiceResVo.getList().forEach(item -> {
            // 转换发票类型
            item.setInvoiceType(typeMap.get(item.getInvoiceType()));
            item.setIsImport(State.ONE.value().equals(item.getState()) ? State.YES.value() : State.NO.value());
            // 转换单位
            item.setUnit(unitMap.get(item.getUnitId()));
            // 转换区域
            item.setDistrict(unitMap.get(item.getDistrictId()));
            item.setAmountStatus(IsReceived.NOT_RECEIVED.key().equals(item.getAmountStatus())
                    ? IsReceived.NOT_RECEIVED.value()
                    : IsReceived.PART_RECEIVED.key().equals(item.getAmountStatus())
                    ? IsReceived.PART_RECEIVED.value() : IsReceived.RECEIVED.value());
        });
        return invoiceResVo;
    }

    @Override
    public RelocationInvoice getInvoiceDetail(Long id) {
        return invoiceMapper.single(id);
    }

    @Override
    public void updateInvoice(InvoiceResVO invoiceVo) {
        Integer re = invoiceMapper.selectListByNumber(invoiceVo.getInvoiceNumber());
        if (re > 0) {
            throw new InvoiceException(InvoiceErrorCode.RELOCATION_INVOICE_IMPORT_BUSTYPE_ERROR);
        }
        RelocationInvoice invoice = translation(invoiceVo);
        invoiceMapper.updateById(invoice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addInvoice(InvoiceResVO invoiceVo) {
        Integer re = invoiceMapper.selectListByNumber(invoiceVo.getInvoiceNumber());
        if (re > 0) {
            throw new InvoiceException(InvoiceErrorCode.RELOCATION_INVOICE_IMPORT_BUSTYPE_ERROR);
        }
        RelocationInvoice invoice = translation(invoiceVo);
        invoiceMapper.insert(invoice);
        //invoice.getProjectId(),invoice.getPaymentType()
        RelocationIncome relocationIncome = getRelocationIncome(invoice);
        incomeMapper.insert(relocationIncome);
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


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSaveRelocationInvoice(List<InvoiceImportVO> dataList) {
        List<String> invoiceNumber = invoiceMapper.selectInvoiceNumber();
        // 转换单位
        Map<String, Integer> unitMap = unitApi.getUnitMapByUnitName();
        // 获取发票类型字典
        List<DictVO> type = dictApi.getDict(TypeCode.RELOCATION.value(), DictCode.RELOCATION_INVOICE_TYPE.value());
        Map<String, String> typeMap = type.stream().collect(Collectors.toMap(DictVO::getValue, DictVO::getLabel));
        List<RelocationInvoice> invoiceList = new ArrayList<>();
        List<RelocationIncome> incomeList = new ArrayList<>();
        long count = dataList.stream().distinct().count();
        //文件内查重
        if (count < dataList.size()) {
            throw new InvoiceException(InvoiceErrorCode.RELOCATION_INCOME_EXCEL_EXIST);
        }
        List<String> msg = new ArrayList<>();
        int i = 1;
        for (InvoiceImportVO invoiceImport : dataList) {
            boolean contains = invoiceNumber.contains(invoiceImport.getInvoiceNumber());
            if (contains) {
                msg.add("在excel表中第" + i + "行，数据编号为:" + invoiceImport.getNumber()
                        + "已存在发票表中\n");
            }
            RelocationInvoice invoice = new RelocationInvoice();
            RelocationIncome income = new RelocationIncome();
            //地区默认11
            invoice.setDistrict(11);
            //经办单位
            invoice.setUnitId(unitMap.get(invoiceImport.getUnit()));
            invoice.setInvoiceNumber(invoiceImport.getInvoiceNumber());
            String invoiceType = invoiceImport.getInvoiceType();
            invoice.setInvoiceType(Integer.valueOf(typeMap.get(invoiceType)));
            //开票日期格式转换 yyyy/MM/dd
            invoice.setInvoiceTime(DateUtil.string2DateYMD(invoiceImport.getInvoiceTime()));
            invoice.setAmount(new BigDecimal(invoiceImport.getAmount()));
            //税率 空
            invoice.setTaxRate(new BigDecimal("0"));
            //税额
            invoice.setTaxAmount(new BigDecimal(invoiceImport.getTaxAmount() == null ? "0"
                    : invoiceImport.getTaxAmount()));
            //价税合计
            invoice.setTaxIncludeAmount(new BigDecimal(invoiceImport.getTaxIncludeAmount()));
            //备注
            invoice.setRemake(invoiceImport.getRemake());
            //收款负责人/申请人
            invoice.setApplicant(invoiceImport.getPayee());
            //==============添加 收款信息==================
            BeanUtils.copyProperties(invoiceImport, income);
            income.setReceivable(new BigDecimal(invoiceImport.getReceivable()));
            income.setReceived(new BigDecimal(invoiceImport.getReceived() == null ? "0.0" :
                    invoiceImport.getReceived()));
            income.setUnreceived(new BigDecimal(invoiceImport.getUnreceived() == null ? "0.0" :
                    invoiceImport.getUnreceived()));
            income.setCategory("迁改".equals(invoiceImport.getCategory()) ? 1
                    : "搬迁".equals(invoiceImport.getCategory()) ? 2 : 3);
            income.setUnitId(unitMap.get(invoiceImport.getUnit()));
            //合同起始时间
            income.setStartTime(DateUtil.string3DateYMD(invoiceImport.getStartTime()));
            //合同截止时间
            income.setContractDeadline(DateUtil.string3DateYMD(invoiceImport.getContractDeadline()));
            //开票时间
            income.setInvoiceTime(DateUtil.string3DateYMD(invoiceImport.getInvoiceTime()));
            // 10-已收款 20-未收款 30-部分回款
            income.setIsReceived(IsReceived.NOT_RECEIVED.value().equals(invoiceImport.getCategory())
                    ? IsReceived.NOT_RECEIVED.key()
                    : IsReceived.PART_RECEIVED.value().equals(invoiceImport.getCategory())
                    ? IsReceived.PART_RECEIVED.key() : IsReceived.RECEIVED.key());
            //发票号码
            income.setInvoiceNum(invoiceImport.getInvoiceNumber());
            //税额
            income.setTax(new BigDecimal(invoiceImport.getTaxAmount()));
            invoiceList.add(invoice);
            incomeMapper.insert(income);
            // 收款编号有数据 插入收款详情
            if (!StringUtils.isEmpty(invoiceImport.getReceiptNum())) {
                RelocationIncomeDetail incomeDetail = new RelocationIncomeDetail();
                incomeDetail.setPayMonth(String.valueOf(invoiceImport.getPaymentDay()));
                incomeDetail.setAmount(new BigDecimal(invoiceImport.getReceived()));
                incomeDetail.setCreateTime(DateUtil.getCurrentDate());
                //todo 收款id获取问题
                incomeDetail.setIncomeId(income.getId());
                incomeDetail.setPayee(invoiceImport.getPayee());
                detailMapper.insert(incomeDetail);
            }
        }
        if (!msg.isEmpty()) {
            throw new InvoiceException(InvoiceErrorCode.RELOCATION_INCOME_NUMBER_EXIST, msg.toString());
        }
        invoiceMapper.insertBatch(invoiceList);
    }

    @Override
    public List<InvoiceExportResVO> selectExportListByCondition(InvoiceReqVO vo, Integer userId) {
        setConditionDetail(vo, userId);
        List<InvoiceExportResVO> exportResVos = invoiceMapper.selectExportListByCondition(vo);
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        exportResVos.forEach(item -> {
            item.setInvoiceType(State.ONE.value().equals(item.getInvoiceType()) ? InvoiceType.PLAIN_INVOICE.value()
                    : InvoiceType.SPECIAL_INVOICE.value());
            item.setState(State.ONE.value().equals(item.getState()) ? InvoiceSate.BLUE_STATE.value()
                    : InvoiceSate.RED_STATE.value());
            item.setIsImport(State.ONE.value().equals(item.getState()) ? State.YES.value() : State.NO.value());
            item.setUnit(unitMap.get(item.getUnitId()));
            item.setDistrict(unitMap.get(item.getDistrictId()));
        });
        return exportResVos;
    }

    @Override
    public RelocationInvoice getInvoice(String invoiceNum) {
        return invoiceMapper.selectInvoiceByInvoiceNum(invoiceNum);
    }


    private void setConditionDetail(InvoiceReqVO cond, Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        if (userApi.isAdmin(userId)) {
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
        BeanUtils.copyProperties(invoiceVo, invoice);
        invoice.setInvoiceTime(DateUtil.string2DateYMD(invoiceVo.getInvoiceTime()));
        invoice.setState(Integer.valueOf(invoiceVo.getState()));
        invoice.setIsImport(Integer.valueOf(invoiceVo.getIsImport()));
        invoice.setDistrict(Integer.valueOf(invoiceVo.getDistrict()));
        return invoice;
    }

    private RelocationIncome getRelocationIncome(RelocationInvoice relocationInvoice) {
        RelocationIncome income = new RelocationIncome();
        /*String remake = relocationInvoice.getRemake();
        //按照中文分隔符划分
        List<String> remakeList = Arrays.asList(remake.split("；"));
        if (remakeList.size() != 4) {
            throw new InvoiceException(InvoiceErrorCode.RELOCATION_INVOICE_REMAKE_ERROR);
        }
        if (StringUtils.isEmpty(remakeList.get(0)) || StringUtils.isEmpty(remakeList.get(1))
            || StringUtils.isEmpty(remakeList.get(2)) || StringUtils.isEmpty(remakeList.get(3))) {
            throw new RelocationException(RelocationErrorCode.RELOCATION_INVOICE_REMAKE_ERROR);
        }
        RelocationProject relocationProject = projectMapper.selectOneByContractNum(remakeList.get(0));
        if (relocationProject.getId() == null) {
            //todo 发票匹配不到合同 空则插入失败
        }
        // 发票合同信息获取
        income.setContractNum(relocationProject.getContractNum());
        income.setContractName(relocationProject.getContractName());
        income.setStartTime(relocationProject.getPlanStartTime());
        income.setContractDeadline(relocationProject.getPlanEndTime());
        income.setContractAmount(relocationProject.getCompensationAmount());
        income.setConstructionName(relocationProject.getProjectName());*/
        //类别 默认迁改
        income.setCategory(1);
        income.setUnitId(relocationInvoice.getUnitId());
        income.setSupplier(relocationInvoice.getBuyerName());
        income.setInvoiceTime(relocationInvoice.getInvoiceTime());
        income.setInvoiceNum(relocationInvoice.getInvoiceNumber());
        income.setInvoiceType(relocationInvoice.getInvoiceType() == 1 ? InvoiceType.ELECTRONIC_PLAIN_INVOICE.key()
                : InvoiceType.ELECTRONIC_SPECIAL_INVOICE.key());
        income.setAmount(relocationInvoice.getAmount());
        income.setTax(relocationInvoice.getTaxAmount());
        income.setTaxIncludeAmount(relocationInvoice.getTaxIncludeAmount());
        return income;
    }


}
