package com.hbhb.cw.relocation.web.controller;

import com.alibaba.excel.EasyExcel;
import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.relocation.model.RelocationInvoice;
import com.hbhb.cw.relocation.rpc.FileApiExp;
import com.hbhb.cw.relocation.service.InvoiceService;
import com.hbhb.cw.relocation.service.listener.InvoiceListener;
import com.hbhb.cw.relocation.web.vo.InvoiceExportResVO;
import com.hbhb.cw.relocation.web.vo.InvoiceImportVO;
import com.hbhb.cw.relocation.web.vo.InvoiceReqVO;
import com.hbhb.cw.relocation.web.vo.InvoiceResVO;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.PageResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaokang
 * @since 2020-09-17
 */
@Tag(name = "传输迁改-发票管理")
@RestController
@RequestMapping("/invoice")
@Slf4j
public class InvoiceController {

    @Resource
    private InvoiceService invoiceService;
    @Resource
    private FileApiExp fileApi;

    @Operation(summary = "迁改发票查询台账列表")
    @GetMapping("/list")
    public PageResult<InvoiceResVO> getInvoiceList(
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为20") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "接收参数实体") InvoiceReqVO cond,
            @Parameter(hidden = true) @UserId Integer userId) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 20 : pageSize;
        return invoiceService.getInvoiceList(pageNum, pageSize, cond, userId);
    }

    @Operation(summary = "迁改发票详情查询")
    @GetMapping("/{id}")
    public RelocationInvoice getInvoiceDetail(@PathVariable Long id) {
        return invoiceService.getInvoiceDetail(id);
    }

    @Operation(summary = "新增迁改发票")
    @PostMapping("/add")
    public void addInvoice(@RequestBody InvoiceResVO invoice) {
        invoiceService.addInvoice(invoice);
    }

    @Operation(summary = "修改迁改发票")
    @PutMapping
    public void updateInvoice(@RequestBody InvoiceResVO invoice) {
        invoiceService.updateInvoice(invoice);
    }

    @Operation(summary = "迁改管理发票导入")
    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public List<String> importInvoice(MultipartFile file) {
        long begin = System.currentTimeMillis();
        String fileName = file.getOriginalFilename();
        invoiceService.judgeFileName(fileName);
        try {
            EasyExcel.read(file.getInputStream(), InvoiceImportVO.class,
                    new InvoiceListener(invoiceService)).sheet().headRowNumber(2).doRead();
            List<String> msg = invoiceService.getMsg();
            if (msg.size() != 0) {
                return msg;
            }
        } catch (IOException | NumberFormatException | NullPointerException e) {
            log.error(e.getMessage(), e);
        }
        log.info("迁改发票导入结束，总共耗时：" + (System.currentTimeMillis() - begin) / 1000 + "s");
        return null;
    }

    @Operation(summary = "迁改管理发票导出")
    @PostMapping("/export")
    public void exportInvoice(HttpServletRequest request, HttpServletResponse response,
                              @Parameter(description = "导出筛选条件实体") @RequestBody InvoiceReqVO vo,
                              @Parameter(hidden = true) @UserId Integer userId) {
        List<InvoiceExportResVO> invoiceExportRes = invoiceService
                .selectExportListByCondition(vo, userId);
        String fileName = ExcelUtil.encodingFileName(request, "迁改发票数据表");
        ExcelUtil.export2Web(response, fileName, "迁改发票清单", InvoiceExportResVO.class,
                invoiceExportRes);

    }

    @Operation(summary = "迁改管理发票模板导出")
    @PostMapping("/export/template")
    public void exportInvoiceTemplate(HttpServletRequest request, HttpServletResponse response) {
        List<Object> object = new ArrayList<>();
        String fileName = ExcelUtil.encodingFileName(request, "迁改发票导入模板");
        ExcelUtil.export2WebWithTemplate(response, fileName, "迁改发票导入模板",
                fileApi.getTemplatePath() + File.separator + "迁改发票导入模板.xlsx", object);
    }

    @Operation(summary = "跟据发票编号查看收据详情")
    @GetMapping("/info")
    public RelocationInvoice getInvoice(String invoiceNum) {
        return invoiceService.getInvoice(invoiceNum);
    }
}
