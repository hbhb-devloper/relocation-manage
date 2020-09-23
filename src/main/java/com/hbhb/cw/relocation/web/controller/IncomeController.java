package com.hbhb.cw.relocation.web.controller;

import com.hbhb.cw.relocation.web.vo.IncomeReqVO;
import com.hbhb.cw.relocation.web.vo.IncomeResVO;
import com.hbhb.springboot.web.view.Page;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
@Api(tags = "传输迁改-收款管理")
@RestController
@RequestMapping("/income")
@Slf4j
public class IncomeController {

    @ApiOperation("迁改收款查询台账列表")
    @GetMapping("/list")
    public Page<IncomeResVO> getIncomeList(
            @ApiParam(value = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @ApiParam(value = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            @ApiParam("接收参数实体") IncomeReqVO cond) {

        return null;
    }

    @ApiOperation("迁改管理收款导入")
    @PostMapping("/import")
    public void importIncome(MultipartFile file) {

    }

    @ApiOperation("迁改管理收款导出")
    @PostMapping("/export")
    public void exportIncome(HttpServletRequest request, HttpServletResponse response,
                             @ApiParam("导出筛选条件实体") @RequestBody IncomeResVO vo) {

    }
}
