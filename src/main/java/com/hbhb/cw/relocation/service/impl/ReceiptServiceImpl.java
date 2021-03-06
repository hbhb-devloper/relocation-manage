package com.hbhb.cw.relocation.service.impl;

import com.hbhb.core.bean.BeanConverter;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.InvoiceType;
import com.hbhb.cw.relocation.enums.IsReceived;
import com.hbhb.cw.relocation.enums.PaymentType;
import com.hbhb.cw.relocation.enums.RelocationErrorCode;
import com.hbhb.cw.relocation.enums.UnitAbbr;
import com.hbhb.cw.relocation.exception.RelocationException;
import com.hbhb.cw.relocation.mapper.IncomeMapper;
import com.hbhb.cw.relocation.mapper.ProjectMapper;
import com.hbhb.cw.relocation.mapper.ReceiptMapper;
import com.hbhb.cw.relocation.model.RelocationIncome;
import com.hbhb.cw.relocation.model.RelocationReceipt;
import com.hbhb.cw.relocation.rpc.UserApiExp;
import com.hbhb.cw.relocation.service.ProjectService;
import com.hbhb.cw.relocation.service.ReceiptService;
import com.hbhb.cw.relocation.util.BigDecimalUtil;
import com.hbhb.cw.relocation.web.vo.ContractInfoVO;
import com.hbhb.cw.relocation.web.vo.ReceiptExportVO;
import com.hbhb.cw.relocation.web.vo.ReceiptImportVO;
import com.hbhb.cw.relocation.web.vo.ReceiptReqVO;
import com.hbhb.cw.relocation.web.vo.ReceiptResVO;
import com.hbhb.cw.systemcenter.api.UnitApi;
import com.hbhb.cw.systemcenter.enums.UnitEnum;
import com.hbhb.cw.systemcenter.model.Unit;
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

/**
 * @author wangxiaogang
 */
@Service
@Slf4j
@SuppressWarnings(value = {"unchecked"})
public class ReceiptServiceImpl implements ReceiptService {
    @Resource
    private ReceiptMapper receiptMapper;
    @Resource
    private ProjectService projectService;
    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private UnitApi unitApi;
    @Resource
    private UserApiExp userAip;
    @Resource
    private IncomeMapper incomeMapper;
    private final List<String> msg = new CopyOnWriteArrayList<>();

    @Override
    public PageResult<ReceiptResVO> getReceiptList(ReceiptReqVO cond, Integer pageNum, Integer pageSize, Integer userId) {

        if (UnitEnum.isHangzhou(cond.getUnitId())) {
            cond.setUnitId(null);
        }
        // ??????????????????
        UserInfo user = userAip.getUserInfoById(userId);
        Unit unitInfo = unitApi.getUnitInfo(user.getUnitId());
        if (!UnitEnum.isHangzhou(user.getUnitId())) {
            cond.setUnitId(user.getUnitId());
        }
        if (UnitAbbr.CWB.value().equals(unitInfo.getUnitName())
                || UnitAbbr.WLB.value().equals(unitInfo.getUnitName())) {
            cond.setUnitId(null);
        }
        PageRequest<ReceiptResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<ReceiptResVO> receiptRes = receiptMapper.selectReceiptByCond(cond, request);
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        // ????????????????????????
        Map<Integer, String> statusMap = getPaymentStatus();
        receiptRes.getList().forEach(item -> {
            item.setUnitName(unitMap.get(item.getUnitId()));
            // ????????????
            item.setIsReceived(statusMap.get(parseInt(item.getIsReceived())));
        });
        return receiptRes;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void addSaveRelocationReceipt(List<ReceiptImportVO> dataList) {

        // ??????????????????????????????
        List<ContractInfoVO> contractInfo = projectMapper.selectContractInfo();
        Map<String, ContractInfoVO> contractMap = contractInfo.stream()
                .collect(Collectors.toMap(ContractInfoVO::getContractNum, Function.identity()));

        List<String> error = new CopyOnWriteArrayList<>();
        Map<String, Integer> unitMap = unitApi.getUnitMapByUnitName();
        List<RelocationReceipt> receiptList = new ArrayList<>();

        // ????????????????????????????????????
        List<String> contractNumList = projectService.getContractNumList();
        // ??????????????????
        List<String> receiptNumList = receiptMapper.selectReceiptNum();
        // ???????????????????????????
        int i = 3;
        for (ReceiptImportVO importVos : dataList) {
            String remake = importVos.getRemake();
            remake = remake.replace("???", ";");
            // ???????????????????????????
            List<String> arrList = Arrays.asList(remake.split(";"));
            if (arrList.size() != 4) {
                error.add("?????????excel???" + i + "??????????????????" + remake + "??????");
            }
            // ?????????????????????????????????
            if (receiptNumList.contains(importVos.getReceiptNum())) {
                error.add("excel?????????" + i + "????????????????????????" + importVos.getReceiptNum() + "???????????????????????????????????????????????????????????????");
            }
            // ????????????????????????????????????????????????
            if (!contractNumList.contains(importVos.getContractNum())) {
                error.add("excel?????????" + i + "???,???????????????" + importVos.getContractNum() + "???????????????????????????????????????");
            }
            if (arrList.size() == 4) {

                RelocationReceipt receipt = new RelocationReceipt();
                BeanUtils.copyProperties(importVos, receipt);
                receipt.setUnitId(unitMap.get(importVos.getUnit()));
                receipt.setReceiptTime(DateUtil.string3DateYMD(importVos.getReceiptTime()));

                // ??????????????????
                if (!arrList.get(2).equals(PaymentType.ADVANCE_PAYMENT.value())
                        && !arrList.get(2).equals(PaymentType.FINAL_PARAGRAPH.value())
                        && !arrList.get(2).equals(PaymentType.FINAL_PAYMENT.value())) {
                    error.add("excel??????" + i + "???????????????????????????");
                }
                receiptList.add(receipt);
            }
            i++;
        }
        // ??????????????????????????????????????????????????????
        msg.clear();
        msg.addAll(error);
        if (error.size() == 0) {
            receiptMapper.insertBatch(receiptList);
            // ??????????????????
            List<RelocationIncome> list = new ArrayList<>();
            for (RelocationReceipt receipt : receiptList) {
                List<String> arrList = Arrays.asList(receipt.getRemake().split("???"));
                RelocationIncome income = setRelocationIncome(receipt, contractMap, arrList);
                list.add(income);
            }
            incomeMapper.insertBatch(list);
        }
    }

    @Override
    public List<ReceiptExportVO> export(ReceiptReqVO vo, Integer userId) {
        if (UnitEnum.isHangzhou(vo.getUnitId())) {
            vo.setUnitId(null);
        }

        // ??????????????????
        UserInfo user = userAip.getUserInfoById(userId);
        if (!UnitEnum.isHangzhou(user.getUnitId()) && vo.getUnitId() == null) {
            vo.setUnitId(user.getUnitId());
        }
        List<ReceiptResVO> list = receiptMapper.selectReceiptListByCond(vo);
        // ????????????????????????
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        list.forEach(item -> item.setUnitName(unitMap.get(item.getUnitId())));
        return BeanConverter.copyBeanList(list, ReceiptExportVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRelocationReceipt(ReceiptResVO receiptResVO) {
        // ???????????????????????????
        List<String> list = receiptMapper.selectReceiptNum();
        if (list.contains(receiptResVO.getReceiptNum())) {
            throw new RelocationException(RelocationErrorCode.RELOCATION_RECEIPT_ALREADY_EXIST);
        }

        // ??????????????????????????????
        List<ContractInfoVO> contractInfo = projectMapper.selectContractInfo();
        Map<String, ContractInfoVO> contractMap = contractInfo.stream()
                .collect(Collectors.toMap(ContractInfoVO::getContractNum, Function.identity()));

        // ??????????????????
        List<String> contractNumList = projectService.getContractNumList();
        RelocationReceipt receipt = setReceipt(receiptResVO, contractNumList);
        receiptMapper.insert(receipt);

        // ??????????????????
        List<String> arrList = Arrays.asList(receipt.getRemake().split("???"));
        RelocationIncome income = setRelocationIncome(receipt, contractMap, arrList);
        incomeMapper.insert(income);
    }

    @Override
    public void updateRelocationReceipt(ReceiptResVO receiptResVO) {
        RelocationReceipt receiptInfo = receiptMapper.single(receiptResVO.getId());

        // ??????????????????????????????
        List<ContractInfoVO> contractInfo = projectMapper.selectContractInfo();
        Map<String, ContractInfoVO> contractMap = contractInfo.stream()
                .collect(Collectors.toMap(ContractInfoVO::getContractNum, Function.identity()));

        // ??????????????????
        List<String> contractNumList = projectService.getContractNumList();
        RelocationReceipt receipt = setReceipt(receiptResVO, contractNumList);
        receiptMapper.updateById(receipt);

        // ??????????????????
        String remake = receipt.getRemake();
        remake = remake.replace("???", ";");
        List<String> arrList = Arrays.asList(remake.split(";"));
        RelocationIncome income = setRelocationIncome(receipt, contractMap, arrList);
        RelocationIncome single = incomeMapper.createLambdaQuery()
                .andEq(RelocationIncome::getInvoiceNum, receiptInfo.getReceiptNum())
                .single();
        income.setId(single.getId());
        incomeMapper.updateById(income);
    }

    @Override
    public void deleteReceipt(Long id) {
        receiptMapper.deleteById(id);
    }

    @Override
    public ReceiptResVO getReceipt(String receiptNum) {
        return receiptMapper.selectReceiptByReceiptNum(receiptNum);
    }

    private RelocationReceipt setReceipt(ReceiptResVO receiptResVO, List<String> contractNumList) {
        RelocationReceipt receipt = new RelocationReceipt();
        BeanUtils.copyProperties(receiptResVO, receipt);
        // ??????????????????
        List<String> msg = new ArrayList<>();
        // ???????????????
        String remake = receipt.getRemake();
        // ???????????????????????????
        remake = remake.replace("???", ";");
        List<String> arrList = Arrays.asList(remake.split(";"));
        if (arrList.size() != 4) {
            msg.add("???????????????????????????" + remake + "??????");
        }
        // ??????????????????
        if (!arrList.get(2).equals(PaymentType.ADVANCE_PAYMENT.value())
                && !arrList.get(2).equals(PaymentType.FINAL_PARAGRAPH.value())
                && !arrList.get(2).equals(PaymentType.FINAL_PAYMENT.value())) {
            msg.add("?????????????????????????????????");
        }

        // ????????????????????????????????????????????????

        if (!contractNumList.contains(receipt.getContractNum())) {
            msg.add("???????????????" + receipt.getContractNum() + "???????????????????????????????????????");
        }
        if (msg.size() != 0) {
            throw new RelocationException("80898", msg.toString());
        }
        // ????????????
        receipt.setCompensationAmount(BigDecimalUtil.getBigDecimal(receiptResVO.getCompensationAmount()));
        // ???????????????
        receipt.setPaymentAmount(BigDecimalUtil.getBigDecimal(receiptResVO.getPaymentAmount()));
        // ???????????????
        receipt.setReceiptAmount(BigDecimalUtil.getBigDecimal(receiptResVO.getReceiptAmount()));
        // ???????????????
        receipt.setReceiptTime(DateUtil.string2DateYMD(receiptResVO.getReceiptTime()));
        return receipt;
    }


    private RelocationIncome setRelocationIncome(RelocationReceipt receipt,
                                                 Map<String, ContractInfoVO> contractMap,
                                                 List<String> arrList) {

        RelocationIncome income = new RelocationIncome();
        income.setCategory(1);
        //????????????(??????id)
        income.setUnitId(receipt.getUnitId());
        //????????????
        income.setContractNum(receipt.getContractNum());
        //????????????
        income.setContractName(receipt.getContractName());
        // ?????????
        income.setSupplier(receipt.getSupplier());
        // ????????????
        income.setStartTime(contractMap.get(receipt.getContractNum()).getPlanStartTime());
        // ??????????????????
        income.setContractDeadline(contractMap.get(receipt.getContractNum()).getPlanEndTime());
        // ????????????
        income.setContractAmount(contractMap.get(receipt.getContractNum()).getTotal());
        // ?????????
        income.setConstructionName(arrList.get(3));
        // ???????????????0
        income.setAging(0);
        // ????????????
        Map<String, Integer> payMap = getPaymentType();
        income.setPaymentType(payMap.get(arrList.get(2)));
        // ???????????????
        income.setInvoiceTime(receipt.getReceiptTime());
        // ????????????
        income.setInvoiceNum(receipt.getReceiptNum());
        // ????????????
        income.setInvoiceType(InvoiceType.RECEIPT.key());
        // ??????
        income.setAmount(receipt.getReceiptAmount());
        // ??????
        income.setTax(BigDecimal.ZERO);
        //?????????
        income.setTaxIncludeAmount(receipt.getReceiptAmount());
        // ??????
        BigDecimal receivable = receipt.getReceiptAmount();
        income.setReceivable(receivable);
        // ??????
        income.setReceived(BigDecimal.ZERO);
        // ??????
        BigDecimal unreceived = receipt.getCompensationAmount().subtract(receipt.getPaymentAmount());
        income.setUnreceived(unreceived);
        // ???????????????10-?????????20 - ?????? ???30-???????????????
        if (unreceived.compareTo(BigDecimal.ZERO) == 0) {
            income.setIsReceived(IsReceived.RECEIVED.key());
        } else if (unreceived.compareTo(receivable) == 0) {
            income.setIsReceived(IsReceived.NOT_RECEIVED.key());
        } else {
            income.setIsReceived(IsReceived.PART_RECEIVED.key());
        }
        return income;
    }

    private Map<String, Integer> getPaymentType() {
        Map<String, Integer> paymentTypeMap = new HashMap<>(100);
        paymentTypeMap.put(PaymentType.ADVANCE_PAYMENT.value(), PaymentType.ADVANCE_PAYMENT.key());
        paymentTypeMap.put(PaymentType.FINAL_PARAGRAPH.value(), PaymentType.FINAL_PARAGRAPH.key());
        paymentTypeMap.put(PaymentType.FINAL_PAYMENT.value(), PaymentType.FINAL_PAYMENT.key());
        return paymentTypeMap;
    }

    private Map<Integer, String> getPaymentStatus() {
        // ????????????
        Map<Integer, String> statusMap = new HashMap<>(100);
        statusMap.put(IsReceived.RECEIVED.key(), IsReceived.RECEIVED.value());
        statusMap.put(IsReceived.NOT_RECEIVED.key(), IsReceived.NOT_RECEIVED.value());
        statusMap.put(IsReceived.PART_RECEIVED.key(), IsReceived.PART_RECEIVED.value());
        return statusMap;
    }

    @Override
    public List<String> getMsg() {
        return this.msg;
    }
}
