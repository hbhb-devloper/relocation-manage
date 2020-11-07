package com.hbhb.cw.relocation.web.controller;


import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.relocation.service.StatementService;
import com.hbhb.cw.relocation.web.vo.StatementExportVO;
import com.hbhb.cw.relocation.web.vo.StatementReqVO;
import com.hbhb.cw.relocation.web.vo.StatementResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.PageResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

@Tag(name = "传输迁改-业务报表统计")
@RestController
@RequestMapping("/statement")
@Slf4j
public class StatementController {

    @Value("${file.upload.template}")
    private String filePath;

    @Resource
    private StatementService statementService;

    @Operation(summary = "报表统计列表")
    @GetMapping("/list")
    public PageResult<StatementResVO> getStatementList(
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "查询单位") @RequestParam(required = false) Integer unitId) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 20 : pageSize;
        return statementService.getStatementList(pageNum, pageSize, unitId);
    }

    @Operation(summary = "统计报表导出")
    @PostMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response, @RequestBody StatementReqVO reqVO) {
        List<StatementExportVO> list = statementService.export(reqVO.getUnitId());
        String fileName = ExcelUtil.encodingFileName(request, "业务报表导出模板.xlsx");
        ExcelUtil.export2WebWithTemplate(response, fileName, "网络部导出模板",
                filePath + File.separator + "业务报表导出模板.xlsx", list);
    }
}
