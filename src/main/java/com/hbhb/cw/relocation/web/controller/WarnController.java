package com.hbhb.cw.relocation.web.controller;


import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.relocation.rpc.FileApiExp;
import com.hbhb.cw.relocation.rpc.SysUserApiExp;
import com.hbhb.cw.relocation.service.WarnService;
import com.hbhb.cw.relocation.web.vo.WarnExportVO;
import com.hbhb.cw.relocation.web.vo.WarnFileVO;
import com.hbhb.cw.relocation.web.vo.WarnReqVO;
import com.hbhb.cw.relocation.web.vo.WarnResVO;
import com.hbhb.cw.relocationmange.api.RelocationWarnApi;
import com.hbhb.cw.systemcenter.enums.FileType;
import com.hbhb.cw.systemcenter.vo.FileDetailVO;
import com.hbhb.cw.systemcenter.vo.SysUserInfo;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
public class WarnController  implements RelocationWarnApi {

    @Resource
    private WarnService warnService;

    @Resource
    private FileApiExp fileService;

    @Resource
    private SysUserApiExp userApi;

    @Operation(summary = "预警提示列表")
    @GetMapping("/list")
    public List<WarnResVO> getWarn(@Parameter(description = "预警查询条件") WarnReqVO warnReqVO) {
        return warnService.getWarn(warnReqVO);
    }

    @Operation(summary = "导出预警提示信息")
    @PostMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response, @RequestBody WarnReqVO reqVO) {
        List<WarnExportVO> export = warnService.export(reqVO);
        String fileName = ExcelUtil.encodingFileName(request, "预警信息");
        ExcelUtil.export2Web(response, fileName, fileName, WarnExportVO.class, export);
    }

    @Operation(summary = "预警附件上传")
    @PostMapping(value = "/system", headers = "content-type=multipart/form-data")
    public void uploadSystemFile(@RequestParam(required = false, value = "file") MultipartFile file, @RequestBody WarnFileVO fileVO,
                                 @UserId Integer userId) {
        FileDetailVO detailVO = fileService.uploadFile(file, FileType.FUND_INVOICE_FILE.value());
        SysUserInfo user = userApi.getUserById(userId);
        fileVO.setFileId(detailVO.getId());
        fileVO.setCreateBy(user.getNickName());
        fileVO.setCreateTime(new Date());
        warnService.addWarnFile(fileVO);

    }

    @Override
    public void addWarn() {
        warnService.addSaveWarn();
    }
}
