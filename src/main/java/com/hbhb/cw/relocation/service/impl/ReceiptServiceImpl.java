package com.hbhb.cw.relocation.service.impl;

import com.hbhb.core.bean.BeanConverter;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.InvoiceType;
import com.hbhb.cw.relocation.enums.IsReceived;
import com.hbhb.cw.relocation.enums.PaymentType;
import com.hbhb.cw.relocation.enums.RelocationErrorCode;
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
import static org.springframework.util.StringUtils.isEmpty;

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
        // 判断用户单位
        UserInfo user = userAip.getUserInfoById(userId);
        Unit unitInfo = unitApi.getUnitInfo(user.getUnitId());
        if (!UnitEnum.isHangzhou(user.getUnitId())) {
            cond.setUnitId(user.getUnitId());
        }
        if ("网络部".equals(unitInfo.getUnitName())) {
            cond.setUnitId(null);
        }
        PageRequest<ReceiptResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<ReceiptResVO> receiptRes = receiptMapper.selectReceiptByCond(cond, request);
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        // 获取回款状态类型
        Map<Integer, String> statusMap = getPaymentStatus();
        receiptRes.getList().forEach(item -> {
            item.setUnitName(unitMap.get(item.getUnitId()));
            // 收款状态
            item.setIsReceived(statusMap.get(parseInt(item.getIsReceived())));
        });
        return receiptRes;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void addSaveRelocationReceipt(List<ReceiptImportVO> dataList) {

        // 获取已存在的合同信息
        List<ContractInfoVO> contractInfo = projectMapper.selectContractInfo();
        Map<String, ContractInfoVO> contractMap = contractInfo.stream()
                .collect(Collectors.toMap(ContractInfoVO::getContractNum, Function.identity()));

        List<String> error = new CopyOnWriteArrayList<>();
        Map<String, Integer> unitMap = unitApi.getUnitMapByUnitName();
        List<RelocationReceipt> receiptList = new ArrayList<>();

        // 验证导入合同编号是否存在
        List<String> contractNumList = projectService.getContractNumList();
        // 查询收据编号
        List<String> receiptNumList = receiptMapper.selectReceiptNum();
        // 检验导入数据准确性
        int i = 3;
        for (ReceiptImportVO importVos : dataList) {
            String remake = importVos.getRemake();
            remake = remake.replace("；", ";");
            // 按照英文分隔符划分
            List<String> arrList = Arrays.asList(remake.split(";"));
            if (arrList.size() != 4) {
                error.add("请检查excel第" + i + "备注修改列：" + remake + "格式");
            }
            // 判断收据编号是否已存在
            if (receiptNumList.contains(importVos.getReceiptNum())) {
                error.add("excel表中第" + i + "行，收据编号为：" + importVos.getReceiptNum() + "在收据信息表中已存在！请仔细检查后重新导入");
            }
            // 判断合同编号是否存在基础项目表中
            if (!contractNumList.contains(importVos.getContractNum())) {
                error.add("excel表中第" + i + "行,合同编号：" + importVos.getContractNum() + "在基础信息中不存在请检查！");
            }
            if (arrList.size() == 4) {
                //1.获取基础信息中所对应的数据

                RelocationReceipt receipt = new RelocationReceipt();
                BeanUtils.copyProperties(importVos, receipt);
                receipt.setUnitId(unitMap.get(importVos.getUnit()));
                receipt.setReceiptTime(DateUtil.string3DateYMD(importVos.getReceiptTime()));

                if (receipt.getProjectId() != null) {
                    receiptList.add(receipt);
                }
                if (!isEmpty(importVos) && receipt.getProjectId() == null) {
                    error.add("excel表中" + i + "行数据与基础信息无法匹配,请检查后从新导入！");
                }
                if (!arrList.get(2).equals(PaymentType.ADVANCE_PAYMENT.value())
                        && !arrList.get(2).equals(PaymentType.FINAL_PARAGRAPH.value())
                        && !arrList.get(2).equals(PaymentType.FINAL_PAYMENT.value())) {
                    error.add("excel第：" + i + "行数据款项类型错误");
                }

            }
            i++;
        }
        // 判断是否有错误如果有则不进入方法体内
        msg.clear();
        msg.addAll(error);
        if (error.size() == 0) {
            receiptMapper.insertBatch(receiptList);
            // 新增收款信息
            List<RelocationIncome> list = new ArrayList<>();
            for (RelocationReceipt receipt : receiptList) {
                List<String> arrList = Arrays.asList(receipt.getRemake().split(";"));
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

        // 判断用户单位
        UserInfo user = userAip.getUserInfoById(userId);
        if (!UnitEnum.isHangzhou(user.getUnitId()) && vo.getUnitId() == null) {
            vo.setUnitId(user.getUnitId());
        }
        List<ReceiptResVO> list = receiptMapper.selectReceiptListByCond(vo);
        // 处理单位转换格式
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        list.forEach(item -> item.setUnitName(unitMap.get(item.getUnitId())));
        return BeanConverter.copyBeanList(list, ReceiptExportVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRelocationReceipt(ReceiptResVO receiptResVO) {
        // 判断是否存在该收据
        List<String> list = receiptMapper.selectReceiptNum();
        if (list.contains(receiptResVO.getReceiptNum())) {
            throw new RelocationException(RelocationErrorCode.RELOCATION_RECEIPT_ALREADY_EXIST);
        }

        // 获取已存在的合同信息
        List<ContractInfoVO> contractInfo = projectMapper.selectContractInfo();
        Map<String, ContractInfoVO> contractMap = contractInfo.stream()
                .collect(Collectors.toMap(ContractInfoVO::getContractNum, Function.identity()));

        // 设置收据信息
        RelocationReceipt receipt = setReceipt(receiptResVO);
        receiptMapper.insert(receipt);

        // 设置收款信息
        List<String> arrList = Arrays.asList(receipt.getRemake().split("；"));
        RelocationIncome income = setRelocationIncome(receipt, contractMap, arrList);
        incomeMapper.insert(income);
    }

    @Override
    public void updateRelocationReceipt(ReceiptResVO receiptResVO) {
        RelocationReceipt receiptInfo = receiptMapper.single(receiptResVO.getId());

        // 获取已存在的合同信息
        List<ContractInfoVO> contractInfo = projectMapper.selectContractInfo();
        Map<String, ContractInfoVO> contractMap = contractInfo.stream()
                .collect(Collectors.toMap(ContractInfoVO::getContractNum, Function.identity()));

        // 修改收据信息
        RelocationReceipt receipt = setReceipt(receiptResVO);
        receiptMapper.updateById(receipt);

        // 修改收款信息
        List<String> arrList = Arrays.asList(receipt.getRemake().split("；"));
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

    private RelocationReceipt setReceipt(ReceiptResVO receiptResVO) {
        RelocationReceipt receipt = new RelocationReceipt();
        BeanUtils.copyProperties(receiptResVO, receipt);
        // 新增收据验证
        List<String> msg = new ArrayList<>();
        // 备注列信息
        String remake = receipt.getRemake();
        // 按照英文分隔符划分
        remake = remake.replace("；", ";");
        List<String> arrList = Arrays.asList(remake.split(";"));
        if (arrList.size() != 4) {
            msg.add("请检查备注修改列：" + remake + "格式");
        }
        // 判断款项类型
        if (!arrList.get(2).equals(PaymentType.ADVANCE_PAYMENT.value())
                && !arrList.get(2).equals(PaymentType.FINAL_PARAGRAPH.value())
                && !arrList.get(2).equals(PaymentType.FINAL_PAYMENT.value())) {
            msg.add("备注列数据款项类型错误");
        }

        // 判断合同编号是否存在基础项目表中
        List<String> contractNumList = projectService.getContractNumList();
        if (!contractNumList.contains(receipt.getContractNum())) {
            msg.add("合同编号：" + receipt.getContractNum() + "在基础信息中不存在请检查！");
        }
        if (msg.size() != 0) {
            throw new RelocationException("80898", msg.toString());
        }
        // 赔补金额
        receipt.setCompensationAmount(BigDecimalUtil.getBigDecimal(receiptResVO.getCompensationAmount()));
        // 已到账金额
        receipt.setPaymentAmount(BigDecimalUtil.getBigDecimal(receiptResVO.getPaymentAmount()));
        // 开收据金额
        receipt.setReceiptAmount(BigDecimalUtil.getBigDecimal(receiptResVO.getReceiptAmount()));
        // 开收据时间
        receipt.setReceiptTime(DateUtil.string2DateYMD(receiptResVO.getReceiptTime()));
        return receipt;
    }


    private RelocationIncome setRelocationIncome(RelocationReceipt receipt,
                                                 Map<String, ContractInfoVO> contractMap,
                                                 List<String> arrList) {

        RelocationIncome income = new RelocationIncome();
        income.setCategory(1);
        //经办单位(单位id)
        income.setUnitId(receipt.getUnitId());
        //合同编号
        income.setContractNum(receipt.getContractNum());
        //合同名称
        income.setContractName(receipt.getContractName());
        // 供应商
        income.setSupplier(receipt.getSupplier());
        // 起始时间
        income.setStartTime(contractMap.get(receipt.getContractNum()).getPlanStartTime());
        // 合同截止时间
        income.setContractDeadline(contractMap.get(receipt.getContractNum()).getPlanEndTime());
        // 合同金额
        income.setContractAmount(contractMap.get(receipt.getContractNum()).getTotal());
        // 工程名
        income.setConstructionName(arrList.get(3));
        // 账龄新增为0
        income.setAging(0);
        // 款项类型
        Map<String, Integer> payMap = getPaymentType();
        income.setPaymentType(payMap.get(arrList.get(2)));
        // 开收据时间
        income.setInvoiceTime(receipt.getReceiptTime());
        // 发票号码
        income.setInvoiceNum(receipt.getReceiptNum());
        // 发票类型
        income.setInvoiceType(InvoiceType.RECEIPT.key());
        // 价款
        income.setAmount(receipt.getReceiptAmount());
        // 税额
        income.setTax(BigDecimal.ZERO);
        //税合计
        income.setTaxIncludeAmount(receipt.getReceiptAmount());
        // 应收
        BigDecimal receivable = receipt.getReceiptAmount();
        income.setReceivable(receivable);
        // 已收
        income.setReceived(receipt.getPaymentAmount());
        // 未收
        BigDecimal unreceived = receipt.getCompensationAmount().subtract(receipt.getPaymentAmount());
        income.setUnreceived(unreceived);
        // 收款情况（10-已收，20 - 未收 ，30-部分回款）
        if (unreceived.compareTo(BigDecimal.ZERO) == 0) {
            income.setIsReceived(IsReceived.RECEIVED.key());
        } else if (unreceived.compareTo(receivable) == 0) {
            income.setIsReceived(IsReceived.NOT_RECEIVED.key());
        } else {
            income.setIsReceived(IsReceived.PART_RECEIVED.key());
        }
        // 收款人
        income.setPayee(receipt.getPayee());
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
        // 收款状态
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
