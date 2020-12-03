package com.hbhb.cw.relocation.service.impl;

import com.hbhb.core.bean.BeanConverter;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.InvoiceType;
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
import com.hbhb.cw.relocation.web.vo.ProjectReqVO;
import com.hbhb.cw.relocation.web.vo.ProjectResVO;
import com.hbhb.cw.relocation.web.vo.ReceiptExportVO;
import com.hbhb.cw.relocation.web.vo.ReceiptImportVO;
import com.hbhb.cw.relocation.web.vo.ReceiptReqVO;
import com.hbhb.cw.relocation.web.vo.ReceiptResVO;
import com.hbhb.cw.systemcenter.api.UnitApi;
import com.hbhb.cw.systemcenter.vo.UnitTopVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;

import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

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
        UnitTopVO parentUnit = unitApi.getTopUnit();
        if (parentUnit.getHangzhou().equals(cond.getUnitId())) {
            cond.setUnitId(null);
        }
        // 判断用户单位
        UserInfo user = userAip.getUserInfoById(userId);
        if (!user.getUnitId().equals(parentUnit.getHangzhou())) {
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
        Map<String, Integer> unitMap = unitApi.getUnitMapByName();
        List<RelocationReceipt> receiptList = new ArrayList<>();
        // 验证导入合同编号是否存在
        List<String> contractNumList = projectService.getContractNumList();
        // 查询收据编号
        List<String> receiptNumList = receiptMapper.selectReceiptNum();
        // 检验导入数据准确性
        int i = 1;
        for (ReceiptImportVO importVos : dataList) {
            String remake = importVos.getRemake();
            // 按照英文分隔符划分
            List<String> arrList = Arrays.asList(remake.split(";"));
            // 按照中文分隔符划分
            List<String> brrList = Arrays.asList(remake.split("；"));
            if (arrList.size() != 4 && brrList.size() != 4) {
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
            // todo 缺少与基础信息表进行验证，目前导入数据"备注修改列"数据存在匹配不符待后续数据完善进行匹配验证
            //1.获取基础信息中所对应的数据
            // 2.与导入数据备注修改列进行比较
            i++;
        }
        if (msg.isEmpty()) {
            throw new RelocationException(RelocationErrorCode.RELOCATION_RECEIPT_IMPORT_ERROR, msg.toString());
        }
        dataList.forEach(item -> receiptList.add(RelocationReceipt.builder()
                .category(item.getCategory())
                .compensationAmount(item.getCompensationAmount())
                .contractName(item.getContractName())
                .contractNum(item.getContractNum())
                .paymentAmount(item.getPaymentAmount())
                .paymentDesc(item.getPaymentDesc())
                .receiptAmount(item.getReceiptAmount())
                .receiptTime(DateUtil.string3DateYMD(item.getReceiptTime()))
                .remake(item.getRemake())
                .unitId(unitMap.get(item.getUnit()))
                .build()));
        receiptMapper.insertBatch(receiptList);
        // 新增收款信息
        List<RelocationIncome> income = setRelocationIncome(receiptList);
        incomeMapper.insertBatch(income);
    }

    private List<RelocationIncome> setRelocationIncome(List<RelocationReceipt> receiptList) {
        List<RelocationIncome> list = new ArrayList<>();
        for (RelocationReceipt receipt : receiptList) {
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
            // todo 暂时不匹配
            // 起始时间
            income.setStartTime(new Date());
            // 合同截止时间
            income.setContractDeadline(new Date());
            // 合同金额
            income.setContractAmount(new BigDecimal(0));
            // 工程名
            // income.setConstructionName();
            // 款项类型
            // income.setPaymentType(receipt.getRemake().split(";")[2]);
            // 账龄
            // income.setAging();
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
            list.add(income);
        }
        return list;
    }

    @Override
    public List<ReceiptExportVO> export(ReceiptReqVO vo, Integer userId) {
        UnitTopVO parentUnit = unitApi.getTopUnit();
        if (parentUnit.getHangzhou().equals(vo.getUnitId())) {
            vo.setUnitId(null);
        }
        // 判断用户单位
        UserInfo user = userAip.getUserInfoById(userId);
        if (!user.getUnitId().equals(parentUnit.getHangzhou())) {
            vo.setUnitId(user.getUnitId());
        }

        List<ReceiptResVO> list = receiptMapper.selectReceiptListByCond(vo);

        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        list.forEach(item -> item.setUnitName(unitMap.get(item.getUnitId())));
        return BeanConverter.copyBeanList(list, ReceiptExportVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRelocationReceipt(ReceiptResVO receiptResVO) {
        RelocationReceipt receipt = setReceipt(receiptResVO);
        // 新增收据验证
        List<String> msg = new ArrayList<>();

        String remake = receipt.getRemake();
        // 按照英文分隔符划分
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
        // 1-合同编号
        String contractNum = arrList.get(0);
        // 2-区县
        String unitName = arrList.get(1);
        // 3-项目名称
        String projectName = arrList.get(3);
        Map<String, Integer> unitMap = unitApi.getUnitMapByName();
        Integer unitId = unitMap.get(unitName);
        ProjectReqVO projectVo = new ProjectReqVO();
        projectVo.setUnitId(unitId);
        projectVo.setProjectName(projectName);
        projectVo.setContractNum(contractNum);
        // 跟据备注列值匹配项目基础信息
        List<ProjectResVO> projectRes = projectMapper.selectProjectByCondList(projectVo);
        if (projectRes.size() > 1) {
            throw new RelocationException(RelocationErrorCode.RELOCATION_RECEIPT_CANT_MATCH);
        } else if (projectRes.size() == 0) {
            throw new RelocationException(RelocationErrorCode.RELOCATION_INVOICE_REMAKE_ERROR);
        } else {
            projectRes.forEach(item -> receipt.setProjectId(item.getId()));
        }
        if (msg.size() != 0) {
            throw new RelocationException(RelocationErrorCode.RELOCATION_INVOICE_REMAKE_ERROR, msg.toString());
        }
        receiptMapper.insert(receipt);
        List<RelocationReceipt> receipts = new ArrayList<>();
        receipts.add(receipt);
        List<RelocationIncome> incomes = setRelocationIncome(receipts);
        incomeMapper.insertBatch(incomes);
    }

    @Override
    public void updateRelocationReceipt(ReceiptResVO receiptResVO) {
        RelocationReceipt receipt = setReceipt(receiptResVO);
        receiptMapper.updateById(receipt);
    }

    @Override
    public void deleteReceipt(Long id) {
        receiptMapper.deleteById(id);
    }

    @Override
    public ReceiptResVO getReceipt(String receiptNum) {
        ReceiptResVO receiptResVO = receiptMapper.selectReceiptByReceiptNum(receiptNum);
        return receiptResVO;
    }

    private RelocationReceipt setReceipt(ReceiptResVO receiptResVO) {
        RelocationReceipt receipt = new RelocationReceipt();
        BeanUtils.copyProperties(receiptResVO, receipt);
        // 赔补金额
        receipt.setCompensationAmount(new BigDecimal(receiptResVO.getCompensationAmount()));
        // 已到账金额
        receipt.setPaymentAmount(new BigDecimal(receiptResVO.getPaymentAmount()));
        // 开收据金额
        receipt.setReceiptAmount(new BigDecimal(receiptResVO.getReceiptAmount()));
        // 开收据时间
        receipt.setReceiptTime(DateUtil.string2DateYMD(receiptResVO.getReceiptTime()));
        return receipt;
    }


}
