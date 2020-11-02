package com.hbhb.cw.relocation.web.controller;

import com.alibaba.excel.EasyExcel;
import com.hbhb.cw.relocation.enums.RelocationErrorCode;
import com.hbhb.cw.relocation.exception.RelocationException;
import com.hbhb.cw.relocation.service.ProjectService;
import com.hbhb.cw.relocation.service.listener.ProjectListener;
import com.hbhb.cw.relocation.web.vo.ProjectImportVO;
import com.hbhb.cw.relocation.web.vo.ProjectReqVO;
import com.hbhb.cw.relocation.web.vo.ProjectResVO;
import com.hbhb.cw.relocationmange.api.RelocationProjectApi;
import com.hbhb.cw.systemcenter.api.SysFileApi;
import com.hbhb.cw.systemcenter.enums.FileType;
import com.hbhb.cw.systemcenter.model.SysUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.PageResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author xiaokang
 * @since 2020-09-17
 */
@Tag(name = "传输迁改-基础信息管理")
@RestController
@RequestMapping("/project")
@Slf4j
public class ProjectController implements RelocationProjectApi {

    @Resource
    private ProjectService projectService;

    @Resource
    private SysFileApi fileApi;

    @Operation(summary = "迁改管理基础信息导入")
    @PostMapping("/import")
    public void ProjectImport(MultipartFile file) {
        long begin = System.currentTimeMillis();
        try {
            EasyExcel.read(file.getInputStream(), ProjectImportVO.class,
                    new ProjectListener(projectService)).sheet().doRead();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RelocationException(RelocationErrorCode.RELOCATION_IMPORT_DATE_REPETITION);
        }
        log.info("迁改基础信息导入成功，总共耗时：" + (System.currentTimeMillis() - begin) / 1000 + "s");
    }

    @Operation(summary = "迁改项目信息查询台账列表")
    @GetMapping("/list")
    public PageResult<ProjectResVO> getProjectList(
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "接收参数实体") ProjectReqVO cond) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        return projectService.getRelocationProjectList(cond, pageNum, pageSize);
    }

    @Operation(summary = "修改迁改项目信息")
    @PutMapping("")

    public void updateProject(@RequestBody ProjectResVO projectResVO, SysUser user) {
        projectService.updateRelocationProject(projectResVO, user);
    }

    @Operation(summary = "删除迁改项目信息")
    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id, SysUser user) {
        projectService.deleteRelocationProject(id, user);
    }

    @Operation(summary = "上传文件")
    @PostMapping(value = "/system", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void uploadSystemFile(@RequestPart(required = false, value = "file") MultipartFile[] files) {
        fileApi.uploadFileList(files, FileType.SYSTEM_FILE.value());
    }

    @Override
    public void updateContractDuration() {
        projectService.updateContractDuration();
    }
}
