package com.hbhb.cw.relocation.service.impl;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.InvoiceErrorCode;
import com.hbhb.cw.relocation.enums.InvoiceState;
import com.hbhb.cw.relocation.enums.InvoiceType;
import com.hbhb.cw.relocation.enums.IsReceived;
import com.hbhb.cw.relocation.enums.PaymentType;
import com.hbhb.cw.relocation.enums.RelocationErrorCode;
import com.hbhb.cw.relocation.enums.State;
import com.hbhb.cw.relocation.enums.UnitAbbr;
import com.hbhb.cw.relocation.exception.InvoiceException;
import com.hbhb.cw.relocation.exception.RelocationException;
import com.hbhb.cw.relocation.mapper.IncomeMapper;
import com.hbhb.cw.relocation.mapper.InvoiceMapper;
import com.hbhb.cw.relocation.mapper.ProjectMapper;
import com.hbhb.cw.relocation.model.RelocationIncome;
import com.hbhb.cw.relocation.model.RelocationInvoice;
import com.hbhb.cw.relocation.rpc.DictApiExp;
import com.hbhb.cw.relocation.rpc.UnitApiExp;
import com.hbhb.cw.relocation.rpc.UserApiExp;
import com.hbhb.cw.relocation.service.InvoiceService;
import com.hbhb.cw.relocation.util.BigDecimalUtil;
import com.hbhb.cw.relocation.web.vo.ContractInfoVO;
import com.hbhb.cw.relocation.web.vo.InvoiceExportResVO;
import com.hbhb.cw.relocation.web.vo.InvoiceImportVO;
import com.hbhb.cw.relocation.web.vo.InvoiceReqVO;
import com.hbhb.cw.relocation.web.vo.InvoiceResVO;
import com.hbhb.cw.systemcenter.enums.DictCode;
import com.hbhb.cw.systemcenter.enums.TypeCode;
import com.hbhb.cw.systemcenter.enums.UnitEnum;
import com.hbhb.cw.systemcenter.model.Unit;
import com.hbhb.cw.systemcenter.vo.DictVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * @author xiaokang
 * @since 2020-09-22
 */
@Service
@Slf4j
@SuppressWarnings(value = {"unchecked"})
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
    private final List<String> msg = new CopyOnWriteArrayList<>();

    @Override
    public PageResult<InvoiceResVO> getInvoiceList(Integer pageNum, Integer pageSize,
                                                   InvoiceReqVO cond, Integer userId) {

        if (UnitEnum.isHangzhou(cond.getUnitId())) {
            cond.setUnitId(null);
        }
        UserInfo user = userApi.getUserInfoById(userId);
        Unit unitInfo = unitApi.getUnitInfo(user.getUnitId());
        if (UnitAbbr.CWB.value().equals(unitInfo.getUnitName())
                || UnitAbbr.WLB.value().equals(unitInfo.getUnitName())) {
            cond.setUnitId(null);
        }

        PageRequest<InvoiceResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<InvoiceResVO> invoiceResVo = invoiceMapper.selectListByCondition(cond, request);
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        // ????????????????????????
        List<DictVO> type = dictApi.getDict(TypeCode.RELOCATION.value(),
                DictCode.RELOCATION_INVOICE_TYPE.value());
        Map<String, String> typeMap = type.stream().collect(Collectors.
                toMap(DictVO::getValue, DictVO::getLabel));
        // ????????????????????????
        Map<Integer, String> statusMap = getPaymentStatus();
        invoiceResVo.getList().forEach(item -> {
            // ??????????????????
            item.setInvoiceType(typeMap.get(item.getInvoiceType()));
            // ????????????????????????
            item.setIsImport(State.ONE.value().equals(item.getIsImport()) ?
                    State.YES.value() : State.NO.value());
            // ????????????
            item.setUnit(unitMap.get(item.getUnitId()));
            // ????????????
            item.setDistrict(unitMap.get(item.getDistrictId()));
            // ????????????
            item.setIsReceived(statusMap.get(parseInt(item.getIsReceived())));
            // ????????????
            item.setState(State.ONE.value().equals(item.getState()) ?
                    InvoiceState.RED_STATE.value() : InvoiceState.BLUE_STATE.value());
        });
        return invoiceResVo;
    }

    @Override
    public RelocationInvoice getInvoiceDetail(Long id) {
        return invoiceMapper.single(id);
    }

    @Override
    public void updateInvoice(InvoiceResVO invoiceVo) {

        // ??????????????????
        RelocationInvoice invoiceInfo = invoiceMapper.single(invoiceVo.getId());
        RelocationInvoice invoice = translation(invoiceVo);

        // ??????????????????????????????
        List<ContractInfoVO> contractInfo = projectMapper.selectContractInfo();
        Map<String, ContractInfoVO> contractMap = contractInfo.stream()
                .collect(Collectors.toMap(ContractInfoVO::getContractNum, Function.identity()));

        // ??????????????????
        List<RelocationInvoice> invoiceList = new ArrayList<>();
        invoiceList.add(invoice);
        List<RelocationIncome> incomeList = getRelocationIncome(invoiceList, contractMap);

        // ????????????
        invoiceMapper.updateTemplateById(invoice);
        RelocationIncome single = incomeMapper.createLambdaQuery()
                .andEq(RelocationIncome::getInvoiceNum, invoiceInfo.getInvoiceNumber())
                .single();
        RelocationIncome income = incomeList.get(0);
        income.setId(single.getId());

        // ????????????
        incomeMapper.updateById(income);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addInvoice(InvoiceResVO invoiceVo) {
        Integer re = invoiceMapper.selectListByNumber(invoiceVo.getInvoiceNumber());
        if (re > 0) {
            throw new InvoiceException(InvoiceErrorCode.RELOCATION_INCOME_NUMBER_EXIST);
        }

        // ??????????????????????????????
        List<ContractInfoVO> contractInfo = projectMapper.selectContractInfo();
        Map<String, ContractInfoVO> contractMap = contractInfo.stream()
                .collect(Collectors.toMap(ContractInfoVO::getContractNum, Function.identity()));

        // ???????????????????????????
        RelocationInvoice invoice = translation(invoiceVo);
        invoiceMapper.insert(invoice);

        // ?????????????????????????????????
        List<RelocationInvoice> invoiceList = new ArrayList<>();
        invoiceList.add(invoice);
        List<RelocationIncome> incomeList = getRelocationIncome(invoiceList, contractMap);
        incomeMapper.insert(incomeList.get(0));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void addSaveRelocationInvoice(List<InvoiceImportVO> dataList) {

        // ??????????????????????????????
        List<ContractInfoVO> contractInfo = projectMapper.selectContractInfo();
        List<String> contractNumList = new ArrayList<>();
        contractInfo.forEach(item -> contractNumList.add(item.getContractNum()));
        Map<String, ContractInfoVO> contractMap = contractInfo.stream()
                .collect(Collectors.toMap(ContractInfoVO::getContractNum, Function.identity()));

        // ??????????????????????????????????????????????????????
        List<String> invoiceNumber = invoiceMapper.selectInvoiceNumber();
        // ????????????
        Map<String, Integer> unitMap = unitApi.getUnitMapByUnitName();
        // ????????????????????????
        List<DictVO> type = dictApi.getDict(TypeCode.RELOCATION.value(),
                DictCode.RELOCATION_INVOICE_TYPE.value());
        Map<String, String> typeMap = type.stream()
                .collect(Collectors.toMap(DictVO::getLabel, DictVO::getValue));
        // ??????
        List<RelocationInvoice> invoiceList = new ArrayList<>();

        long count = dataList.stream().distinct().count();
        // ???????????????
        if (count < dataList.size()) {
            throw new InvoiceException(InvoiceErrorCode.RELOCATION_INCOME_EXCEL_EXIST);
        }
        List<String> error = new ArrayList<>();
        int i = 3;
        for (InvoiceImportVO invoiceImport : dataList) {
            RelocationInvoice invoice = new RelocationInvoice();
            BeanUtils.copyProperties(invoiceImport, invoice);
            if (invoiceNumber.contains(invoiceImport.getInvoiceNumber())) {
                error.add("???excel?????????" + i + "?????????????????????:" +
                        invoiceImport.getInvoiceNumber() + "?????????????????????,??????????????????????????????");
            }
            // ???????????????
            String remake = invoice.getRemake();
            // ???????????????????????????
            remake = remake.replace("???", ";");
            List<String> arrList = Arrays.asList(remake.split(";"));
            if (arrList.size() != 4) {
                error.add("?????????excel???" + i + "????????????????????????" + remake + "??????");
            }
            if (arrList.size() == 4) {
                // 1-????????????
                String contractNum = arrList.get(0);
                if (!contractNumList.contains(contractNum)) {
                    error.add("excel??????" + i + "?????????????????????????????????????????????");
                }
                if (!arrList.get(2).equals(PaymentType.ADVANCE_PAYMENT.value())
                        && !arrList.get(2).equals(PaymentType.FINAL_PARAGRAPH.value())
                        && !arrList.get(2).equals(PaymentType.FINAL_PAYMENT.value())) {
                    error.add("excel??????" + i + "???????????????????????????");
                }
                // ???????????????????????????
                // ????????????11
                invoice.setDistrict(11);
                // ????????????
                invoice.setUnitId(unitMap.get(invoiceImport.getUnitId()));
                // ????????????
                invoice.setInvoiceNumber(invoiceImport.getInvoiceNumber());
                String invoiceType = invoiceImport.getInvoiceType();
                invoice.setInvoiceType(Integer.valueOf(typeMap.get(invoiceType)));
                // ???????????????????????? yyyy/MM/dd
                invoice.setInvoiceTime(DateUtil.string3DateYMD(invoiceImport.getInvoiceTime()));
                // ??????
                invoice.setAmount(BigDecimalUtil.getBigDecimal(invoiceImport.getAmount()));
                // ?????? ???
                invoice.setTaxRate(isEmpty(invoiceImport.getTaxRate())
                        ? BigDecimal.ZERO : BigDecimalUtil.getBigDecimal(invoiceImport.getTaxRate()));
                //????????????
                invoice.setState(invoiceImport.getState().equals(InvoiceState.RED_STATE.value())
                        ? InvoiceState.RED_STATE.key() : InvoiceState.BLUE_STATE.key());
                // ??????
                invoice.setTaxAmount(BigDecimalUtil.getBigDecimal(invoiceImport.getTaxAmount()));
                // ????????????
                invoice.setTaxIncludeAmount(BigDecimalUtil.getBigDecimal(invoiceImport.getTaxIncludeAmount()));
                // ??????
                invoice.setRemake(invoiceImport.getRemake());
                //??????????????????????????????
                invoice.setIsImport(State.NO.value().equals(invoiceImport.getIsImport())
                        ? Integer.parseInt(State.ZERO.value()) : Integer.parseInt(State.ONE.value()));
                // ???????????????/?????????
                invoice.setApplicant(invoiceImport.getApplicant());
                // ??????
                invoiceList.add(invoice);
            }
            i++;
        }
        // ??????????????????
        msg.clear();
        msg.addAll(error);
        if (error.size() == 0) {
            // ?????????????????????????????????
            List<RelocationIncome> income = getRelocationIncome(invoiceList, contractMap);
            incomeMapper.insertBatch(income);
            invoiceMapper.insertBatch(invoiceList);
        }

    }

    @Override
    public List<InvoiceExportResVO> selectExportListByCondition(InvoiceReqVO cond, Integer userId) {

        // ??????????????????
        UserInfo user = userApi.getUserInfoById(userId);
        Unit unitInfo = unitApi.getUnitInfo(user.getUnitId());

        if (UnitEnum.isHangzhou(cond.getUnitId())) {
            cond.setUnitId(null);
        }

        if (UnitAbbr.CWB.value().equals(unitInfo.getUnitName())
                || UnitAbbr.WLB.value().equals(unitInfo.getUnitName())) {
            cond.setUnitId(null);
        }

        List<InvoiceExportResVO> exportResVos = invoiceMapper.selectExportListByCondition(cond);
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        // ?????????????????? ??????????????????????????????
        int i = 1;
        for (InvoiceExportResVO export : exportResVos) {

            // ??????
            export.setNum(i);

            // ????????????
            export.setInvoiceType(State.ONE.value().equals(export.getInvoiceType()) ?
                    InvoiceType.PLAIN_INVOICE.value() : InvoiceType.SPECIAL_INVOICE.value());

            // ????????????
            export.setState(State.ONE.value().equals(export.getState()) ?
                    InvoiceState.BLUE_STATE.value() : InvoiceState.RED_STATE.value());

            // ?????????????????????
            export.setIsImport(State.ONE.value().equals(export.getState()) ?
                    State.YES.value() : State.NO.value());

            // ??????
            export.setUnit(unitMap.get(export.getUnitId()));

            // ??????
            export.setDistrict(unitMap.get(export.getDistrictId()));
            i++;
        }
        return exportResVos;
    }

    @Override
    public RelocationInvoice getInvoice(String invoiceNum) {
        return invoiceMapper.selectInvoiceByInvoiceNum(invoiceNum);
    }


    private RelocationInvoice translation(InvoiceResVO invoiceVo) {
        String remake = invoiceVo.getRemake();
        remake = remake.replace("???", ";");
        List<String> arrList = Arrays.asList(remake.split(";"));
        if (arrList.size() != 4) {
            throw new InvoiceException(InvoiceErrorCode.RELOCATION_INVOICE_REMAKE_ERROR);
        }
        RelocationInvoice invoice = new RelocationInvoice();
        // ??????
        BeanUtils.copyProperties(invoiceVo, invoice);
        invoice.setInvoiceTime(DateUtil.string2DateYMD(invoiceVo.getInvoiceTime()));
        invoice.setState(Integer.valueOf(invoiceVo.getState()));
        invoice.setIsImport(Integer.valueOf(invoiceVo.getIsImport()));
        invoice.setDistrict(Integer.valueOf(invoiceVo.getDistrict()));
        invoice.setInvoiceType(Integer.valueOf(invoiceVo.getInvoiceType()));
        return invoice;
    }

    private List<RelocationIncome> getRelocationIncome(List<RelocationInvoice> invoiceList,
                                                       Map<String, ContractInfoVO> contractMap) {
        List<RelocationIncome> incomeList = new ArrayList<>();
        for (RelocationInvoice invoice : invoiceList) {
            RelocationIncome income = new RelocationIncome();
            String remake = invoice.getRemake();
            // ???????????????????????????
            remake = remake.replace("???", ";");
            List<String> arrList = Arrays.asList(remake.split(";"));
            // ????????????????????????
            income.setContractNum(contractMap.get(arrList.get(0)).getContractNum());
            income.setContractName(contractMap.get(arrList.get(0)).getContractName());
            income.setStartTime(contractMap.get(arrList.get(0)).getPlanStartTime());
            income.setContractDeadline(contractMap.get(arrList.get(0)).getPlanEndTime());
            income.setContractAmount(contractMap.get(arrList.get(0)).getTotal());
            income.setConstructionName(arrList.get(3));
            //?????? ????????????
            income.setCategory(1);
            income.setUnitId(invoice.getUnitId());
            income.setSupplier(invoice.getBuyerName());
            income.setInvoiceTime(invoice.getInvoiceTime());
            income.setInvoiceNum(invoice.getInvoiceNumber());
            income.setInvoiceType(invoice.getInvoiceType());
            income.setAmount(invoice.getAmount());
            income.setTax(invoice.getTaxAmount());
            // ????????????
            Map<String, Integer> payMap = getPaymentType();
            income.setPaymentType(payMap.get(arrList.get(2)));
            // ????????????
            income.setTaxIncludeAmount(invoice.getTaxIncludeAmount());
            // ??????????????????????????????????????????
            income.setIsReceived(IsReceived.NOT_RECEIVED.key());
            // ??????
            income.setReceivable(invoice.getAmount());
            // ??????
            income.setReceived(BigDecimal.ZERO);
            // ??????
            income.setUnreceived(invoice.getAmount());
            // ?????????
            income.setProjectId(invoice.getProjectId());
            incomeList.add(income);

        }
        return incomeList;
    }

    private Map<Integer, String> getPaymentStatus() {
        // ????????????
        Map<Integer, String> statusMap = new HashMap<>(100);
        statusMap.put(IsReceived.RECEIVED.key(), IsReceived.RECEIVED.value());
        statusMap.put(IsReceived.NOT_RECEIVED.key(), IsReceived.NOT_RECEIVED.value());
        statusMap.put(IsReceived.PART_RECEIVED.key(), IsReceived.PART_RECEIVED.value());
        return statusMap;
    }

    private Map<String, Integer> getPaymentType() {
        Map<String, Integer> paymentTypeMap = new HashMap<>(100);
        paymentTypeMap.put(PaymentType.ADVANCE_PAYMENT.value(), PaymentType.ADVANCE_PAYMENT.key());
        paymentTypeMap.put(PaymentType.FINAL_PARAGRAPH.value(), PaymentType.FINAL_PARAGRAPH.key());
        paymentTypeMap.put(PaymentType.FINAL_PAYMENT.value(), PaymentType.FINAL_PAYMENT.key());
        return paymentTypeMap;
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
    public List<String> getMsg() {
        return this.msg;
    }
}
