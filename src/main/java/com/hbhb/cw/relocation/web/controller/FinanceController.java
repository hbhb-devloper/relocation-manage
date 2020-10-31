package com.hbhb.cw.relocation.web.controller;


import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.relocation.enums.InvoiceErrorCode;
import com.hbhb.cw.relocation.exception.InvoiceException;
import com.hbhb.cw.relocation.service.FinanceService;
import com.hbhb.cw.relocation.web.vo.FinanceReqVO;
import com.hbhb.cw.relocation.web.vo.FinanceResVO;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.annotation.Resource;
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
    private FinanceService financeService;

    @Operation(summary = "涉财统计列表")
    @GetMapping("/list")
    public PageResult<FinanceResVO> getFinanceList(
        @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
        @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
        @Parameter(description = "接收参数实体") FinanceReqVO cond,
        @Parameter(hidden = true) @UserId Integer userId) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        PageResult<FinanceResVO> financeList = null;
        try {
            financeList = financeService.getFinanceList(pageNum, pageSize, cond, userId);
        } catch (UnsupportedEncodingException e) {
            throw new InvoiceException(InvoiceErrorCode.RELOCATION_FINANCE_QUERY_PARAM_ERROR);
        }
        return financeList;
    }

    @Operation(summary = "涉财统计导出")
    @PostMapping("/export")
    public void exportIncome(HttpServletRequest request, HttpServletResponse response,
        @Parameter(description = "接收参数实体") @RequestBody FinanceReqVO cond,
        @Parameter(hidden = true) @UserId Integer userId) {
        List<FinanceResVO> financeExport = financeService
            .selectExportListByCondition(cond, userId);
        String fileName = ExcelUtil.encodingFileName(request, "涉财统计数据表");
        ExcelUtil.export2Web(response, fileName, "迁改款涉财统计清单", FinanceResVO.class,
            financeExport);
    }
}
