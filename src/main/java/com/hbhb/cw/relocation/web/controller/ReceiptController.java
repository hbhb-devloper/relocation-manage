package com.hbhb.cw.relocation.web.controller;

import com.alibaba.excel.EasyExcel;
import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.relocation.enums.RelocationErrorCode;
import com.hbhb.cw.relocation.exception.RelocationException;
import com.hbhb.cw.relocation.service.ReceiptService;
import com.hbhb.cw.relocation.service.listener.ReceiptListener;
import com.hbhb.cw.relocation.web.vo.ReceiptExportVO;
import com.hbhb.cw.relocation.web.vo.ReceiptImportVO;
import com.hbhb.cw.relocation.web.vo.ReceiptReqVO;
import com.hbhb.cw.relocation.web.vo.ReceiptResVO;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.PageResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author xiaokang
 * @since 2020-09-17
 */
@Tag(name = "传输迁改-收据管理")
@RestController
@RequestMapping("/receipt")
@Slf4j
public class ReceiptController {

    @Resource
    private ReceiptService receiptService;

    @Operation(summary = "迁改收据查询台账列表")
    @GetMapping("/list")
    public PageResult<ReceiptResVO> getReceiptList(
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "接收参数实体") ReceiptReqVO cond, @UserId Integer userId) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        return receiptService.getReceiptList(cond, pageNum, pageSize, userId);
    }

    @Operation(summary = "新增迁改收据")
    @PostMapping("")
    public void saveReceipt(@Parameter(description = "收据信息实体") @RequestBody ReceiptResVO receiptResVO) {
        receiptService.addRelocationReceipt(receiptResVO);
    }

    @Operation(summary = "修改迁改收据")
    @PutMapping("")
    public void updateReceipt(@Parameter(description = "收据信息实体") @RequestBody ReceiptResVO receiptResVO) {
        receiptService.updateRelocationReceipt(receiptResVO);
    }

    @Operation(summary = "删除迁改收据")
    @DeleteMapping("/{id}")
    public void deleteReceipt(@PathVariable Long id) {
        receiptService.deleteReceipt(id);
    }

    @Operation(summary = "迁改管理收据导入")
    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void importReceipt(MultipartFile file) {
        long begin = System.currentTimeMillis();
        try {
            EasyExcel.read(file.getInputStream(), ReceiptImportVO.class,
                    new ReceiptListener(receiptService)).sheet().headRowNumber(2).doRead();
        } catch (IOException | NumberFormatException | NullPointerException e) {
            log.error(e.getMessage(), e);
            throw new RelocationException(RelocationErrorCode.RELOCATION_RECEIPT_IMPORT_ERROR);
        }
        log.info("迁改收据模板导入成功，总共耗时：" + (System.currentTimeMillis() - begin) / 1000 + "s");

    }

    @Operation(summary = "迁改管理收据导出")
    @PostMapping("/export")
    public void exportReceipt(HttpServletRequest request, HttpServletResponse response,
                              @Parameter(description = "导出筛选条件实体") @RequestBody ReceiptReqVO vo, @UserId Integer userId) {
        List<ReceiptExportVO> list = receiptService.export(vo, userId);
        String fileName = ExcelUtil.encodingFileName(request, "签报列表");
        ExcelUtil.export2Web(response, fileName, fileName, ReceiptExportVO.class, list);
    }
}
