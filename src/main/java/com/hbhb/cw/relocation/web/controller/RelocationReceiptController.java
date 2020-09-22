package com.hbhb.cw.relocation.web.controller;

import com.hbhb.springboot.web.view.Page;
import com.hbhb.cw.relocation.web.vo.ReceiptReqVO;
import com.hbhb.cw.relocation.web.vo.ReceiptResVO;

import org.apache.ibatis.annotations.Param;
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
@Api(tags = "传输迁改-收据管理")
@RestController
@RequestMapping("/relocation/receipt")
@Slf4j
public class RelocationReceiptController {

    @ApiOperation("迁改收据查询台账列表")
    @GetMapping("/list")
    public Page<ReceiptResVO> getReceiptList(
            @ApiParam(value = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @ApiParam(value = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            @Param("接收参数实体") ReceiptReqVO cond) {

        return null;
    }

    @ApiOperation(value = "新增迁改收据")
    @PostMapping("")
    public void saveReceipt() {

    }

    @ApiOperation(value = "修改迁改收据")
    @PutMapping("")
    public void updateReceipt() {

    }

    @ApiOperation(value = "删除迁改收据")
    @DeleteMapping("/{id}")
    public void deleteReceipt(@PathVariable Long id) {

    }

    @ApiOperation("迁改管理收据导入")
    @PostMapping("/import")
    public void importReceipt(MultipartFile file) {

    }

    @ApiOperation("迁改管理收据导出")
    @PostMapping("/export")
    public void exportReceipt(HttpServletRequest request, HttpServletResponse response,
                              @ApiParam("导出筛选条件实体") @RequestBody ReceiptResVO vo) {

    }
}
