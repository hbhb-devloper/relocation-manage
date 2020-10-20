package com.hbhb.cw.relocation.web.controller;

import com.hbhb.cw.relocation.web.vo.ProjectReqVO;
import com.hbhb.cw.relocation.web.vo.ProjectResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.beetl.sql.core.page.PageResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xiaokang
 * @since 2020-09-17
 */
@Tag(name = "传输迁改-基础信息管理")
@RestController
@RequestMapping("/project")
@Slf4j
public class ProjectController {

    @Operation(summary = "迁改项目信息查询台账列表")
    @GetMapping("/list")
    public PageResult<ProjectResVO> getProjectList(
        @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
        @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
        @Parameter(description = "接收参数实体") ProjectReqVO cond) {

        return null;
    }

    @Operation(summary = "修改迁改项目信息")
    @PutMapping("")
    public void updateProject(@RequestBody ProjectResVO projectResVO) {

    }

    @Operation(summary = "删除迁改项目信息")
    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id) {

    }

    @Operation(summary = "迁改管理基础信息导入")
    @PostMapping("/import")
    public void importProject(MultipartFile file) {

    }
}
