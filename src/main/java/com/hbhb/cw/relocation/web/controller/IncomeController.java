package com.hbhb.cw.relocation.web.controller;

import com.alibaba.excel.EasyExcel;
import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.relocation.enums.InvoiceErrorCode;
import com.hbhb.cw.relocation.exception.InvoiceException;
import com.hbhb.cw.relocation.model.RelocationIncomeDetail;
import com.hbhb.cw.relocation.rpc.FileApiExp;
import com.hbhb.cw.relocation.service.IncomeService;
import com.hbhb.cw.relocation.service.listener.IncomeListener;
import com.hbhb.cw.relocation.web.vo.IncomeExportVO;
import com.hbhb.cw.relocation.web.vo.IncomeImportVO;
import com.hbhb.cw.relocation.web.vo.IncomeReqVO;
import com.hbhb.cw.relocation.web.vo.IncomeResVO;
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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaokang
 * @since 2020-09-17
 */
@Tag(name = "传输迁改-收款管理")
@RestController
@RequestMapping("/income")
@Slf4j
public class IncomeController {

    @Resource
    private IncomeService incomeService;
    @Resource
    private FileApiExp fileApi;

    @Operation(summary = "迁改收款查询台账列表")
    @GetMapping("/list")
    public PageResult<IncomeResVO> getIncomeList(
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "接收参数实体") IncomeReqVO cond,
            @Parameter(hidden = true) @UserId Integer userId) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        return incomeService.getIncomeList(pageNum, pageSize, cond, userId);
    }

    @Operation(summary = "迁改管理收款导入")
    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public List<String> importIncome(MultipartFile file) {
        long begin = System.currentTimeMillis();
        String fileName = file.getOriginalFilename();
        incomeService.judgeFileName(fileName);
        try {
            EasyExcel.read(file.getInputStream(), IncomeImportVO.class,
                    new IncomeListener(incomeService)).sheet().headRowNumber(2).doRead();
            List<String> msg = incomeService.getMsg();
            if (msg.size() != 0) {
                return msg;
            }
        } catch (IOException | NumberFormatException | NullPointerException e) {
            log.error(e.getMessage(), e);
            throw new InvoiceException(InvoiceErrorCode.RELOCATION_INCOME_IMPORT_ERROR);
        }
        log.info("迁改收款导入结束，总共耗时：" + (System.currentTimeMillis() - begin) / 1000 + "s");
        return null;
    }

    @Operation(summary = "迁改管理收款导出")
    @PostMapping("/export")
    public void exportIncome(HttpServletRequest request, HttpServletResponse response,
                             @Parameter(description = "导出筛选条件实体") @RequestBody IncomeReqVO vo,
                             @Parameter(hidden = true) @UserId Integer userId) {
        List<IncomeExportVO> incomeExport = incomeService.selectExportListByCondition(vo, userId);
        String fileName = ExcelUtil.encodingFileName(request, "迁改收款数据表");
        ExcelUtil.export2Web(response, fileName, "迁改收款清单", IncomeExportVO.class,
                incomeExport);
    }

    @Operation(summary = "迁改管理收款模板下载")
    @PostMapping("/export/template")
    public void exportTemplate(HttpServletRequest request, HttpServletResponse response) {
        List<Object> object = new ArrayList<>();
        String fileName = ExcelUtil.encodingFileName(request, "迁改收款导入模板");
        ExcelUtil.export2WebWithTemplate(response, fileName, "迁改收款导入模板",
                fileApi.getTemplatePath() + File.separator + "迁改收款导入模板.xlsx", object);

    }

    @Operation(summary = "迁改收款详情查询")
    @GetMapping("/detail")
    public List<RelocationIncomeDetail> getIncomeDetail(@Parameter(description = "收款数据id") Long id,
                                                        @Parameter(description = "查询是否为本月") Integer isNeed) {
        return incomeService.getIncomeDetail(id, isNeed);
    }

    @Operation(summary = "修改收款详情")
    @PutMapping("/updateIncome")
    public void updateIncomeDetail(@RequestBody RelocationIncomeDetail detail) {
        incomeService.updateIncomeDetail(detail);
    }

    @Operation(summary = "新增收款清单")
    @PostMapping("/addDetail")
    public void addIncomeDetail(@RequestBody RelocationIncomeDetail detail,
                                @Parameter(hidden = true) @UserId Integer userId) {
        incomeService.addIncomeDetail(detail, userId);
    }
}
