package com.hbhb.cw.relocation.web.controller;


import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.relocation.api.RelocationWarnApi;
import com.hbhb.cw.relocation.model.RelocationFile;
import com.hbhb.cw.relocation.rpc.FileApiExp;
import com.hbhb.cw.relocation.rpc.UserApiExp;
import com.hbhb.cw.relocation.service.WarnService;
import com.hbhb.cw.relocation.web.vo.WarnExportVO;
import com.hbhb.cw.relocation.web.vo.WarnFileResVO;
import com.hbhb.cw.relocation.web.vo.WarnReqVO;
import com.hbhb.cw.relocation.web.vo.WarnResVO;
import com.hbhb.cw.systemcenter.enums.FileType;
import com.hbhb.cw.systemcenter.vo.FileVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import com.hbhb.web.annotation.UserId;

import org.beetl.sql.core.page.PageResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "传输迁改-预警管理")
@RestController
@RequestMapping("/warn")
public class WarnController implements RelocationWarnApi {

    @Resource
    private WarnService warnService;
    @Resource
    private UserApiExp userApi;
    @Resource
    private FileApiExp fileApi;

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
    public FileVO uploadSystemFile(@RequestPart(required = false, value = "file") MultipartFile file) {
        return fileApi.upload(file, FileType.RELOCATION_WARN_FILE.value());
    }

    @Operation(summary = "预警附件保存")
    @PostMapping(value = "/file")
    public void saveWarnFile(@Parameter(description = "预警id") @RequestParam Long warnId,
                             @Parameter(description = "附件id") @RequestParam Long fileId,
                             @Parameter(hidden = true) @UserId Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        warnService.addWarnFile(RelocationFile.builder()
                .warnId(warnId)
                .fileId(fileId)
                .createBy(user.getNickName())
                .createTime(new Date())
                .build());
    }

    @Operation(summary = "预警附件查看")
    @GetMapping("/file")
    public List<WarnFileResVO> getWarnFile(Long warnId) {
        return warnService.getWarnFileList(warnId);
    }

    @Operation(summary = "预警信息统计")
    @GetMapping("/total")
    public Integer getTotalInfo(@RequestParam("单位id") Integer unitId) {
        return warnService.getWarnCount(unitId);
    }

    @Override
    public void addWarn() {
        warnService.addSaveWarn();
    }

    @Override
    public int getWarnCount(Integer unitId) {
        return warnService.getWarnCount(unitId);
    }

    @Operation(summary = "预警提示列表")
    @GetMapping("/tree-list")
    public PageResult<WarnResVO> getWarnList(
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "预警查询条件") WarnReqVO warnReqVO, @UserId Integer userId) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 30 : pageSize;
        return warnService.getWarnList(warnReqVO, userId, pageNum, pageSize);
    }

}


