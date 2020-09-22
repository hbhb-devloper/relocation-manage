package com.hbhb.cw.relocation.web.controller;

import com.hbhb.springboot.web.view.Page;
import com.hbhb.cw.relocation.web.vo.ProjectReqVO;
import com.hbhb.cw.relocation.web.vo.ProjectResVO;

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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xiaokang
 * @since 2020-09-17
 */
@Api(tags = "传输迁改-基础信息管理")
@RestController
@RequestMapping("/relocation/project")
@Slf4j
public class RelocationProjectController {

    @ApiOperation("迁改项目信息查询台账列表")
    @GetMapping("/list")
    public Page<ProjectResVO> getProjectList(
            @ApiParam(value = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @ApiParam(value = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            @ApiParam("接收参数实体") ProjectReqVO cond) {

        return null;
    }

    @ApiOperation("修改迁改项目信息")
    @PutMapping("")
    public void updateProject(@RequestBody ProjectResVO projectResVO) {

    }

    @ApiOperation("删除迁改项目信息")
    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id) {

    }

    @ApiOperation("迁改管理基础信息导入")
    @PostMapping("/import")
    public void importProject(MultipartFile file) {

    }
}
