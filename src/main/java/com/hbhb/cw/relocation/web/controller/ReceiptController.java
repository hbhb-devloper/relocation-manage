package com.hbhb.cw.relocation.web.controller;

import com.hbhb.cw.relocation.web.vo.ReceiptReqVO;
import com.hbhb.cw.relocation.web.vo.ReceiptResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "传输迁改-收据管理")
@RestController
@RequestMapping("/receipt")
@Slf4j
public class ReceiptController {

    @Operation(summary = "迁改收据查询台账列表")
    @GetMapping("/list")
    public PageResult<ReceiptResVO> getReceiptList(
        @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
        @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
        @Parameter(description = "接收参数实体") ReceiptReqVO cond) {

        return null;
    }

    @Operation(summary = "新增迁改收据")
    @PostMapping("")
    public void saveReceipt() {

    }

    @Operation(summary = "修改迁改收据")
    @PutMapping("")
    public void updateReceipt() {

    }

    @Operation(summary = "删除迁改收据")
    @DeleteMapping("/{id}")
    public void deleteReceipt(@PathVariable Long id) {

    }

    @Operation(summary = "迁改管理收据导入")
    @PostMapping("/import")
    public void importReceipt(MultipartFile file) {

    }

    @Operation(summary = "迁改管理收据导出")
    @PostMapping("/export")
    public void exportReceipt(HttpServletRequest request, HttpServletResponse response,
        @Parameter(description = "导出筛选条件实体") @RequestBody ReceiptResVO vo) {

    }
}
