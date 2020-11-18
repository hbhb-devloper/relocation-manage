package com.hbhb.cw.relocation.web.controller;


import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.relocation.api.RelocationWarnApi;
import com.hbhb.cw.relocation.rpc.FileApiExp;
import com.hbhb.cw.relocation.rpc.SysUserApiExp;
import com.hbhb.cw.relocation.service.WarnService;
import com.hbhb.cw.relocation.web.vo.*;
import com.hbhb.cw.systemcenter.enums.FileType;
import com.hbhb.cw.systemcenter.vo.FileDetailVO;
import com.hbhb.cw.systemcenter.vo.SysUserInfo;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Tag(name = "传输迁改-预警管理")
@RestController
@RequestMapping("/warn")
public class WarnController implements RelocationWarnApi {

    @Resource
    private WarnService warnService;

    @Resource
    private FileApiExp fileService;

    @Resource
    private SysUserApiExp userApi;

    @Operation(summary = "预警提示列表")
    @GetMapping("/list")
    public List<WarnResVO> getWarn(@Parameter(description = "预警查询条件") WarnReqVO warnReqVO, @UserId Integer userId) {
        return warnService.getWarn(warnReqVO, userId);
    }

    @Operation(summary = "导出预警提示信息")
    @PostMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response, @RequestBody WarnReqVO reqVO) {
        List<WarnExportVO> export = warnService.export(reqVO);
        String fileName = ExcelUtil.encodingFileName(request, "预警信息");
        ExcelUtil.export2Web(response, fileName, fileName, WarnExportVO.class, export);
    }

    @Operation(summary = "预警附件上传")
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public FileDetailVO uploadSystemFile(@RequestPart(required = false, value = "file") MultipartFile file) {
        return fileService.uploadFile(file, FileType.FUND_INVOICE_FILE.value());
    }

    @Operation(summary = "预警附件保存")
    @PostMapping(value = "/save")
    public void andWarnFile(@Parameter(description = "预警附件信息") @RequestBody WarnFileVO fileVO, @UserId Integer userId) {
        SysUserInfo user = userApi.getUserById(userId);
        fileVO.setCreateBy(user.getNickName());
        fileVO.setCreateTime(new Date());
        warnService.addWarnFile(fileVO);
    }

    @Operation(summary = "预警信息统计")
    @GetMapping("/total")
    public Integer getTotalInfo(@RequestParam("单位id") Integer unitId) {
        return warnService.getWarnCount(unitId);
    }

    @Operation(summary = "预警附件查看")
    @GetMapping("/file")
    public List<WarnFileResVO> getWarnFile(Long warnId) {
        return warnService.getWarnFileList(warnId);
    }

    @Override
    public void addWarn() {
        warnService.addSaveWarn();
    }

    @Override
    public int getWarnCount(Integer unitId) {
        return warnService.getWarnCount(unitId);

    }
}


