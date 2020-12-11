package com.hbhb.cw.relocation.service.impl;

import com.hbhb.core.bean.BeanConverter;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.InvoiceType;
import com.hbhb.cw.relocation.enums.PaymentType;
import com.hbhb.cw.relocation.enums.RelocationErrorCode;
import com.hbhb.cw.relocation.exception.RelocationException;
import com.hbhb.cw.relocation.mapper.IncomeMapper;
import com.hbhb.cw.relocation.mapper.ProjectMapper;
import com.hbhb.cw.relocation.mapper.ReceiptMapper;
import com.hbhb.cw.relocation.model.RelocationIncome;
import com.hbhb.cw.relocation.model.RelocationProject;
import com.hbhb.cw.relocation.model.RelocationReceipt;
import com.hbhb.cw.relocation.rpc.UserApiExp;
import com.hbhb.cw.relocation.service.ProjectService;
import com.hbhb.cw.relocation.service.ReceiptService;
import com.hbhb.cw.relocation.util.BigDecimalUtil;
import com.hbhb.cw.relocation.web.vo.*;
import com.hbhb.cw.systemcenter.api.UnitApi;
import com.hbhb.cw.systemcenter.enums.UnitEnum;
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
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author wangxiaogang
 */
@Service
@Slf4j
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

    @Override
    public PageResult<ReceiptResVO> getReceiptList(ReceiptReqVO cond, Integer pageNum, Integer pageSize, Integer userId) {

        if (UnitEnum.isHangzhou(cond.getUnitId())) {
            cond.setUnitId(null);
        }
        // 判断用户单位
        UserInfo user = userAip.getUserInfoById(userId);
        if (!UnitEnum.isHangzhou(user.getUnitId())) {
            cond.setUnitId(user.getUnitId());
        }
        PageRequest<ReceiptResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<ReceiptResVO> receiptRes = receiptMapper.selectReceiptByCond(cond, request);
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        receiptRes.getList().forEach(item -> item.setUnitName(unitMap.get(item.getUnitId())));
        return receiptRes;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void addSaveRelocationReceipt(List<ReceiptImportVO> dataList) {
        List<String> msg = new CopyOnWriteArrayList<>();
        Map<String, Integer> unitMap = unitApi.getUnitMapByUnitName();
        List<RelocationReceipt> receiptList = new ArrayList<>();
        // 验证导入合同编号是否存在
        List<String> contractNumList = projectService.getContractNumList();
        // 查询收据编号
        List<String> receiptNumList = receiptMapper.selectReceiptNum();
        // 检验导入数据准确性
        int i = 1;
        for (ReceiptImportVO importVos : dataList) {
            String remake = importVos.getRemake();
            remake = remake.replace("；", ";");
            // 按照英文分隔符划分
            List<String> arrList = Arrays.asList(remake.split(";"));
            if (arrList.size() != 4) {
                msg.add("请检查备注修改列：" + remake + "格式");
            }
            // 判断收据编号是否已存在
            if (receiptNumList.contains(importVos.getReceiptNum())) {
                msg.add("excel表中第" + i + "行，收据编号为：" + importVos.getReceiptNum() + "在收据信息表中已存在！请仔细检查后重新导入");
            }
            // 判断合同编号是否存在基础项目表中
            if (!contractNumList.contains(importVos.getContractNum())) {
                msg.add("合同编号：" + importVos.getContractNum() + "在基础信息中不存在请检查！");
            }
            //1.获取基础信息中所对应的数据
            List<ProjectReqVO> projectReq = getProjectResVo(arrList, unitMap);
            RelocationReceipt receipt = new RelocationReceipt();
            BeanUtils.copyProperties(importVos, receipt);
            receipt.setUnitId(unitMap.get(importVos.getUnit()));
            receipt.setReceiptTime(DateUtil.string3DateYMD(importVos.getReceiptTime()));
            if (projectReq.size() == 1) {
                projectReq.forEach(item -> receipt.setProjectId(item.getId()));
            }
            if (receipt.getProjectId() != null) {
                receiptList.add(receipt);
            }
            i++;
        }
        if (!msg.isEmpty()) {
            throw new RelocationException(RelocationErrorCode.RELOCATION_IMPORT_DATE_ERROR, msg.toString());
        }
        receiptMapper.insertBatch(receiptList);
        // 新增收款信息
        List<RelocationIncome> list = new ArrayList<>();
        for (RelocationReceipt receipt : receiptList) {
            RelocationIncome income = setRelocationIncome(receipt);
            list.add(income);
        }
        incomeMapper.insertBatch(list);
    }

    @Override
    public List<ReceiptExportVO> export(ReceiptReqVO vo, Integer userId) {
        if (UnitEnum.isHangzhou(vo.getUnitId())) {
            vo.setUnitId(null);
        }
        // 判断用户单位
        UserInfo user = userAip.getUserInfoById(userId);
        if (UnitEnum.isHangzhou(user.getUnitId())) {
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
        RelocationReceipt receipt = setReceipt(receiptResVO);
        receiptMapper.insert(receipt);
        RelocationIncome incomes = setRelocationIncome(receipt);
        incomeMapper.insert(incomes);
    }

    @Override
    public void updateRelocationReceipt(ReceiptResVO receiptResVO) {
        RelocationReceipt receipt = setReceipt(receiptResVO);
        receiptMapper.updateById(receipt);
        // 修改收款信息
        RelocationIncome income = setRelocationIncome(receipt);
        RelocationIncome single = incomeMapper.createLambdaQuery()
                .andEq(RelocationIncome::getInvoiceNum, receiptResVO.getReceiptNum())
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
        // 判断合同编号是否存在基础项目表中
        List<String> contractNumList = projectService.getContractNumList();
        if (!contractNumList.contains(receipt.getContractNum())) {
            msg.add("合同编号：" + receipt.getContractNum() + "在基础信息中不存在请检查！");
        }
        // 判断备注列数据是否对应基础信息
        // 转换单位
        Map<String, Integer> unitMap = unitApi.getUnitMapByUnitName();
        List<ProjectReqVO> projectRes = getProjectResVo(arrList, unitMap);
        if (projectRes.size() > 1) {
            throw new RelocationException(RelocationErrorCode.RELOCATION_RECEIPT_CANT_MATCH);
        } else if (projectRes.size() == 0) {
            throw new RelocationException(RelocationErrorCode.RELOCATION_RECEIPT_REMAKE_ERROR);
        } else {
            projectRes.forEach(item -> receipt.setProjectId(item.getId()));
        }
        if (msg.size() != 0) {
            throw new RelocationException(RelocationErrorCode.RELOCATION_IMPORT_DATE_ERROR, msg.toString());
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

    private List<ProjectReqVO> getProjectResVo(List<String> remake, Map<String, Integer> unitMap) {
        // 判断备注列数据是否对应基础信息
        // 1-合同编号
        String contractNum = remake.get(0);
        // 2-区县
        String unitName = remake.get(1);
        // 3-项目名称
        String projectName = remake.get(3);
        Integer unitId = unitMap.get(unitName);
        ProjectReqVO projectVo = new ProjectReqVO();
        projectVo.setUnitId(unitId);
        projectVo.setProjectName(projectName);
        projectVo.setContractNum(contractNum);
        //  通过备注修改列对比基础项目信息表
        return projectMapper.selectProjectByCondList(projectVo);
    }


    private RelocationIncome setRelocationIncome(RelocationReceipt receipt) {
        RelocationProject project = projectMapper.single(receipt.getProjectId());
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
        income.setStartTime(project.getPlanStartTime());
        // 合同截止时间
        income.setContractDeadline(project.getActualEndTime());
        // 合同金额
        income.setContractAmount(project.getCompensationAmount());
        // 工程名
        income.setConstructionName(project.getProjectName());
        // 账龄新增为0
        income.setAging(0);
        // 款项类型
        String remake = receipt.getRemake();
        remake = remake.replace("；", ";");
        List<String> arrList = Arrays.asList(remake.split(";"));
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
        income.setTax(new BigDecimal(0));
        //税合计
        income.setTaxIncludeAmount(receipt.getReceiptAmount());
        // 收款情况（0-未收款、1-已收款）
        income.setIsReceived(0);
        // 应收
        income.setReceivable(receipt.getReceiptAmount());
        // 已收
        income.setReceived(receipt.getPaymentAmount());
        // 未收
        income.setUnreceived(receipt.getCompensationAmount().subtract(receipt.getPaymentAmount()));
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

}
