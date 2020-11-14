package com.hbhb.cw.relocation.service.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.hbhb.cw.relocation.enums.RelocationErrorCode;
import com.hbhb.cw.relocation.exception.RelocationException;
import com.hbhb.cw.relocation.service.ReceiptService;
import com.hbhb.cw.relocation.web.vo.ReceiptImportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wxg
 * @since 2020-09-26
 */
@Slf4j
@SuppressWarnings(value = {"rawtypes"})
public class ReceiptListener extends AnalysisEventListener {

    /**
     * 批处理条数，每隔多少条清理一次list ，方便内存回收
     */
    private static final int BATCH_COUNT = 500;

    /**
     * 数据行
     */
    private final List<ReceiptImportVO> dataList = new ArrayList<>();

    private final ReceiptService receiptService;


    public ReceiptListener(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    /**
     * 每次读取一条数据时调用该方法
     */
    @Override
    public void invoke(Object object, AnalysisContext context) {
        dataList.add((ReceiptImportVO) object);
        if (dataList.size() >= BATCH_COUNT) {
            saveData();
            dataList.clear();
        }
    }

    /**
     * 所有数据解析完成后调用该方法
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 确保最后一次的数据入库
        saveData();
        dataList.clear();
    }

    /**
     * 保存预算数据
     */
    private void saveData() {
        if (!CollectionUtils.isEmpty(dataList)) {
            receiptService.addSaveRelocationReceipt(dataList);
        }
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        throw   new RelocationException(RelocationErrorCode.RELOCATION_IMPORT_DATE_ERROR);
    }

}