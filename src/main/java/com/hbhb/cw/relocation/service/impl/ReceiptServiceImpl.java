package com.hbhb.cw.relocation.service.impl;

import com.hbhb.core.bean.BeanConverter;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.RelocationErrorCode;
import com.hbhb.cw.relocation.exception.RelocationException;
import com.hbhb.cw.relocation.mapper.ReceiptMapper;
import com.hbhb.cw.relocation.model.RelocationReceipt;
import com.hbhb.cw.relocation.service.ProjectService;
import com.hbhb.cw.relocation.service.ReceiptService;
import com.hbhb.cw.relocation.web.vo.ReceiptExportVO;
import com.hbhb.cw.relocation.web.vo.ReceiptImportVO;
import com.hbhb.cw.relocation.web.vo.ReceiptReqVO;
import com.hbhb.cw.relocation.web.vo.ReceiptResVO;
import com.hbhb.cw.systemcenter.api.UnitApi;
import com.hbhb.cw.systemcenter.model.Unit;
import com.hbhb.cw.systemcenter.vo.ParentVO;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

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
    private UnitApi unitApi;

    @Override
    public PageResult<ReceiptResVO> getReceiptList(ReceiptReqVO cond, Integer pageNum, Integer pageSize) {
        ParentVO parentUnit = unitApi.getParentUnit();
        if (parentUnit.getHangzhou().equals(cond.getUnitId())) {
            cond.setUnitId(null);
        }
        PageRequest<ReceiptResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<ReceiptResVO> receiptRes = receiptMapper.selectReceiptByCond(cond, request);
        List<Unit> unitList = getUnitList();
        Map<Integer, String> unitMap = unitList.stream().collect(Collectors.toMap(Unit::getId, Unit::getUnitName));
        receiptRes.getList().forEach(item -> item.setUnitName(unitMap.get(item.getUnitId())));
        return receiptRes;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void addSaveRelocationReceipt(List<ReceiptImportVO> dataList) {
        List<String> msg = new CopyOnWriteArrayList<>();
        List<Unit> list = getUnitList();
        Map<String, Integer> unitMap = list.stream()
                .collect(Collectors.toMap(Unit::getUnitName, Unit::getId));
        List<RelocationReceipt> receiptList = new ArrayList<>();
        // 验证导入编号是否存在
        List<String> contractNumList = projectService.getContractNumList();
        // 检验导入数据准确性
        for (ReceiptImportVO importVos : dataList) {
            String remake = importVos.getRemake();
            // 按照英文分隔符划分
            List<String> arrList = Arrays.asList(remake.split(";"));
            // 按照中文分隔符划分
            List<String> brrList = Arrays.asList(remake.split("；"));
            if (arrList.size() != 4 && brrList.size() != 4) {
                msg.add("请检查备注修改列：" + remake + "格式");
            }
            // 判断合同编号是否存在基础项目表中
            if (!contractNumList.contains(importVos.getContractNum())) {
                msg.add("合同编号：" + importVos.getContractNum() + "在基础信息中不存在请检查！");
            }
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

    }

    @Override
    public List<ReceiptExportVO> export(ReceiptReqVO vo) {
        List<ReceiptResVO> list = receiptMapper.selectReceiptListByCond(vo);
        return BeanConverter.copyBeanList(list, ReceiptExportVO.class);
    }

    @Override
    public void addRelocationReceipt(ReceiptResVO receiptResVO) {
        RelocationReceipt receipt = setReceipt(receiptResVO);
        // 新增收据验证
        List<String> msg = new ArrayList<>();
        List<String> contractNumList = projectService.getContractNumList();
        String remake = receipt.getRemake();
        // 按照英文分隔符划分
        List<String> arrList = Arrays.asList(remake.split(";"));
        // 按照中文分隔符划分
        List<String> brrList = Arrays.asList(remake.split("；"));
        if (arrList.size() != 4 && brrList.size() != 4) {
            msg.add("请检查备注修改列：" + remake + "格式");
        }
        // 判断合同编号是否存在基础项目表中
        if (!contractNumList.contains(receipt.getContractNum())) {
            msg.add("合同编号：" + receipt.getContractNum() + "在基础信息中不存在请检查！");
        }
        if (msg.size() != 0) {
            throw new RelocationException(RelocationErrorCode.RELOCATION_RECEIPT_IMPORT_ERROR, msg.toString());
        }
        receiptMapper.insert(receipt);
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

    private List<Unit> getUnitList() {
        return unitApi.getAllUnitList();
    }
}
