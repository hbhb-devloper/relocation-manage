package com.hbhb.cw.relocation.web.controller;

import com.hbhb.cw.relocation.service.InvoiceService;
import com.hbhb.cw.relocation.web.vo.InvoiceReqVO;
import com.hbhb.cw.relocation.web.vo.InvoiceResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import javafx.scene.effect.Light.Distant;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.PageResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @Operation(summary = "迁改发票查询台账列表")
    @GetMapping("/list")
    public PageResult<InvoiceResVO> getInvoiceList(
        @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
        @Parameter(description = "每页数量，默认为20") @RequestParam(required = false) Integer pageSize,
        @Parameter(description = "接收参数实体") InvoiceReqVO cond) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 20 : pageSize;
        return invoiceService.getInvoiceList(pageNum, pageSize, cond);
    }

    @Operation(summary = "新增迁改发票")
    @PostMapping("")
    public void saveInvoice() {

    }

    @Operation(summary = "修改迁改发票")
    @PutMapping("")
    public void updateInvoice() {

    }

    @Operation(summary = "删除迁改发票")
    @DeleteMapping("/{id}")
    public void deleteInvoice(@PathVariable Long id) {

    }

    @Operation(summary = "迁改管理发票导入")
    @PostMapping("/import")
    public void importInvoice(MultipartFile file) {

    }

    @Operation(summary = "迁改管理发票导出")
    @PostMapping("/export")
    public void exportInvoice(HttpServletRequest request, HttpServletResponse response,
        @Parameter(description = "导出筛选条件实体") @RequestBody InvoiceResVO vo) {

    }
}
