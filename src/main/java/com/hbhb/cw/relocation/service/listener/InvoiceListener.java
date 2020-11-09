package com.hbhb.cw.relocation.service.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.hbhb.cw.relocation.service.InvoiceService;
import com.hbhb.cw.relocation.web.vo.InvoiceImportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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
    private static final int BATCH_COUNT = 10000;


    /**
     * 数据行
     */
    private final List<InvoiceImportVO> dataList = new ArrayList<>();

    private final InvoiceService invoiceService;


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
}
