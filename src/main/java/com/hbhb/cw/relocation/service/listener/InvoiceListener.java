package com.hbhb.cw.relocation.service.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.hbhb.cw.relocation.exception.RelocationException;
import com.hbhb.cw.relocation.service.InvoiceService;
import com.hbhb.cw.relocation.web.vo.InvoiceImportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hyk
 * @since 2020-09-26
 */
@Slf4j
@SuppressWarnings(value = {"rawtypes"})
public class InvoiceListener extends AnalysisEventListener {

    /**
     * 批处理条数，每隔多少条清理一次list ，方便内存回收
     */
    private static final int BATCH_COUNT = 500;


    /**
     * 数据行
     */
    private final List<InvoiceImportVO> dataList = new ArrayList<>();

    private final InvoiceService invoiceService;
    private final Map<Integer, String> importHeadMap = new HashMap<>();


    public InvoiceListener(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    /**
     * 每次读取一条数据时调用该方法
     */
    @Override
    public void invoke(Object object, AnalysisContext context) {
        dataList.add((InvoiceImportVO) object);
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
            invoiceService.addSaveRelocationInvoice(dataList);
        }
    }

    @Override
    public void invokeHeadMap(Map headMap, AnalysisContext context) {
        if (headMap != null) {
            importHeadMap.putAll(headMap);
        }
        // 根据自己的情况去做表头的判断即可
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.info("解析出错：" + exception.getMessage());
        int row = 0, column = 0;
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException convertException = (ExcelDataConvertException) exception;
            row = convertException.getRowIndex();
            column = convertException.getColumnIndex();
            log.error("解析出错：{}行 {}列", row, column);
            String msg = "解析出错啦！ " + "第" + row + "行,第" + column + "列,请检查数据格式或模板是否正确";
            throw new RelocationException("80898", msg);
        }
    }

}
