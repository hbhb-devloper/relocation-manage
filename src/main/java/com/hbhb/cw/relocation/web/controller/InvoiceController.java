package com.hbhb.cw.relocation.web.controller;

import com.hbhb.cw.relocation.service.InvoiceService;
import com.hbhb.cw.relocation.web.vo.InvoiceReqVO;
import com.hbhb.cw.relocation.web.vo.InvoiceResVO;
import com.hbhb.springboot.web.view.Page;

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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xiaokang
 * @since 2020-09-17
 */
@Api(tags = "传输迁改-发票管理")
@RestController
@RequestMapping("/invoice")
@Slf4j
public class InvoiceController {

    @Resource
    private InvoiceService invoiceService;

    @ApiOperation("迁改发票查询台账列表")
    @GetMapping("/list")
    public Page<InvoiceResVO> getInvoiceList(
            @ApiParam(value = "页码，默认为1") @RequestParam(required = false) Long pageNum,
            @ApiParam(value = "每页数量，默认为10") @RequestParam(required = false) Long pageSize,
            @ApiParam("接收参数实体") InvoiceReqVO cond) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 20 : pageSize;
        return invoiceService.getInvoiceList(pageNum, pageSize, cond);
    }

    @ApiOperation(value = "新增迁改发票")
    @PostMapping("")
    public void saveInvoice() {

    }

    @ApiOperation(value = "修改迁改发票")
    @PutMapping("")
    public void updateInvoice() {

    }

    @ApiOperation(value = "删除迁改发票")
    @DeleteMapping("/{id}")
    public void deleteInvoice(@PathVariable Long id) {

    }

    @ApiOperation("迁改管理发票导入")
    @PostMapping("/import")
    public void importInvoice(MultipartFile file) {

    }

    @ApiOperation("迁改管理发票导出")
    @PostMapping("/export")
    public void exportInvoice(HttpServletRequest request, HttpServletResponse response,
                              @ApiParam("导出筛选条件实体") @RequestBody InvoiceResVO vo) {

    }
}
