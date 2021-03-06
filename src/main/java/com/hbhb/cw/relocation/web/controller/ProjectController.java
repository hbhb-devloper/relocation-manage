package com.hbhb.cw.relocation.web.controller;

import com.alibaba.excel.EasyExcel;
import com.hbhb.api.core.bean.FileVO;
import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.relocation.api.RelocationProjectApi;
import com.hbhb.cw.relocation.enums.RelocationErrorCode;
import com.hbhb.cw.relocation.exception.RelocationException;
import com.hbhb.cw.relocation.rpc.FileApiExp;
import com.hbhb.cw.relocation.service.ProjectService;
import com.hbhb.cw.relocation.service.listener.ProjectListener;
import com.hbhb.cw.relocation.web.vo.ProjectImportVO;
import com.hbhb.cw.relocation.web.vo.ProjectReqVO;
import com.hbhb.cw.relocation.web.vo.ProjectResVO;
import com.hbhb.cw.systemcenter.enums.FileType;
import com.hbhb.cw.systemcenter.model.SysUser;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private FileApiExp fileApi;


    @Operation(summary = "迁改管理基础信息导入")
    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public List<String> projectImport(@RequestPart(required = false, value = "file") MultipartFile file) {
        long begin = System.currentTimeMillis();
        projectService.judgeFileName(file.getOriginalFilename());
        try {
            EasyExcel.read(file.getInputStream(), ProjectImportVO.class,
                    new ProjectListener(projectService)).sheet().headRowNumber(2).doRead();
            List<String> msg = projectService.getMsg();
            if (msg.size() != 0) {
                return msg;
            }
        } catch (IOException | NumberFormatException | NullPointerException e) {
            log.error(e.getMessage(), e);
            throw new RelocationException(RelocationErrorCode.RELOCATION_IMPORT_DATE_ERROR);
        }
        log.info("迁改基础信息导入成功，总共耗时：" + (System.currentTimeMillis() - begin) / 1000 + "s");
        return null;
    }

    @Operation(summary = "迁改项目信息查询台账列表")
    @GetMapping("/list")
    public PageResult<ProjectResVO> getProjectList(
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "接收参数实体") ProjectReqVO cond,
            @Parameter(hidden = true) @UserId Integer userId) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        return projectService.getRelocationProjectList(cond, pageNum, pageSize, userId);
    }

    @Operation(summary = "修改迁改项目信息")
    @PutMapping("")
    public void updateProject(@RequestBody ProjectResVO projectResVO, SysUser user) {
        projectService.updateRelocationProject(projectResVO, user);
    }

    @Operation(summary = "删除迁改项目信息")
    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id, @UserId Integer userId) {
        projectService.deleteRelocationProject(id, userId);
    }

    @Override
    public void updateContractDuration() {
        projectService.updateContractDuration();
    }

    @Operation(summary = "批量删除迁改项目信息")
    @DeleteMapping("/batch")
    public void deleteBatch(List<Long> ids) {
        projectService.deleteBatch(ids);
    }

    @Operation(summary = "迁改项目信息详情")
    @GetMapping("/{id}")
    public ProjectResVO getProject(@PathVariable Long id) {
        return projectService.getProject(id);
    }

    @Operation(summary = "基础项目信息模板导出")
    @PostMapping("/export")
    public void getProjectTemplate(HttpServletRequest request, HttpServletResponse response) {
        List<Object> list = new ArrayList<>();
        String fileName = ExcelUtil.encodingFileName(request, "迁改基础信息导入模板");
        ExcelUtil.export2WebWithTemplate(response, fileName, "基础信息导入模板",
                fileApi.getTemplatePath() + File.separator + "迁改基础信息导入模板.xlsx", list);
    }

    @Operation(summary = "上传合同")
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void upload(@RequestPart(required = false, value = "file") MultipartFile file) {
        Boolean a = projectService.judgeContractNum(file);
        if (a) {
            FileVO files = fileApi.upload(file, FileType.RELOCATION_CONTRACT_FILE.value());
            projectService.updateContractFileId(files);
        } else {
            throw new RelocationException("80898", file.getOriginalFilename() + "未匹配导入失败");
        }
    }

    @Operation(summary = "按条件导出基础信息")
    @PostMapping("/export/list")
    public void export(HttpServletRequest request, HttpServletResponse response,
                       @Parameter(description = "接收参数实体") @RequestBody ProjectReqVO cond,
                       @Parameter(hidden = true) @UserId Integer userId) {
        PageResult<ProjectResVO> page = projectService.getRelocationProjectList(cond,
                1, Integer.MAX_VALUE, userId);
        Map<Boolean, String> hasCompensation = new HashMap<>(5);
        hasCompensation.put(true, "有");
        hasCompensation.put(false, "无");
        List<ProjectResVO> list = page.getList();
        list.forEach(item -> item.setHasCompensations(hasCompensation.get(item.getHasCompensation())));
        String fileName = ExcelUtil.encodingFileName(request, "迁改基础信息导出模板");
        ExcelUtil.export2WebWithTemplate(response, fileName, "基础信息导出模板",
                fileApi.getTemplatePath() + File.separator + "迁改基础信息导出模板.xlsx", list);
    }

}

