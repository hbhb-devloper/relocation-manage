package com.hbhb.cw.relocation.web.controller;

import com.hbhb.cw.relocation.web.vo.IncomeReqVO;
import com.hbhb.cw.relocation.web.vo.IncomeResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.PageResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xiaokang
 * @since 2020-09-17
 */
@Tag(name = "传输迁改-收款管理")
@RestController
@RequestMapping("/income")
@Slf4j
public class IncomeController {

    @Operation(summary = "迁改收款查询台账列表")
    @GetMapping("/list")
    public PageResult<IncomeResVO> getIncomeList(
        @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
        @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
        @Parameter(description = "接收参数实体") IncomeReqVO cond) {

        return null;
    }

    @Operation(summary = "迁改管理收款导入")
    @PostMapping("/import")
    public void importIncome(MultipartFile file) {

    }

    @Operation(summary = "迁改管理收款导出")
    @PostMapping("/export")
    public void exportIncome(HttpServletRequest request, HttpServletResponse response,
        @Parameter(description = "导出筛选条件实体") @RequestBody IncomeResVO vo) {

    }
}
