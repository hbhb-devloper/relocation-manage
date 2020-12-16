package com.hbhb.cw.relocation.web.controller;


import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.relocation.rpc.FileApiExp;
import com.hbhb.cw.relocation.service.FinanceService;
import com.hbhb.cw.relocation.web.vo.FinanceReqVO;
import com.hbhb.cw.relocation.web.vo.FinanceResVO;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.PageResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

/**
 * @author hyk
 * @since 2020-10-9
 */
@Tag(name = "传输迁改-涉财统计")
@RestController
@RequestMapping("/refinance")
@Slf4j
public class FinanceController {

    @Resource
    private FileApiExp fileApi;

    @Resource
    private FinanceService financeService;

    @Operation(summary = "涉财统计列表")
    @GetMapping("/list")
    public PageResult<FinanceResVO> getFinanceList(
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "接收参数实体") FinanceReqVO cond,
            Integer userId) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        return financeService.getFinanceList(pageNum, pageSize, cond, userId);
    }

    @Operation(summary = "涉财统计导出")
    @PostMapping("/export")
    public void exportIncome(HttpServletRequest request, HttpServletResponse response,
                             @Parameter(description = "接收参数实体") @RequestBody FinanceReqVO cond,
                             @Parameter(hidden = true) @UserId Integer userId) {
        String path = fileApi.getTemplatePath();

        List<FinanceResVO> list = financeService.selectExportListByCondition(cond, userId);
        String fileName = ExcelUtil.encodingFileName(request, "涉财报表导出模板");
        log.info("模板路径：" + path + fileName);
        ExcelUtil.export2WebWithTemplate(response, fileName, "财务导出报表",
                fileApi.getTemplatePath() + File.separator + "涉财报表导出模板.xlsx", list);
    }
}
