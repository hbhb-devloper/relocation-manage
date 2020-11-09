package com.hbhb.cw.relocation.service;

import com.hbhb.cw.relocation.web.vo.ProjectImportVO;
import com.hbhb.cw.relocation.web.vo.ProjectReqVO;
import com.hbhb.cw.relocation.web.vo.ProjectResVO;
import com.hbhb.cw.systemcenter.model.SysUser;
import org.beetl.sql.core.page.PageResult;

import java.util.List;

/**
 * @author wangxiaogang
 */
public interface ProjectService {
    /**
     * 批量导入传输迁改信息
     *
     * @param importVOList 迁改信息集合
     */
    void addSaveRelocationProject(List<ProjectImportVO> importVOList);

    /**
     * 跟据条件分页查询迁改台账列表
     *
     * @param cond     条件
     * @param pageNum  页数
     * @param pageSize 条数
     * @return 列表
     */
    PageResult<ProjectResVO> getRelocationProjectList(ProjectReqVO cond, Integer pageNum, Integer pageSize);

    /**
     * 修改迁改项目信息
     *
     * @param projectResVO 修改信息实体
     * @param user         登录用户
     */
    void updateRelocationProject(ProjectResVO projectResVO, SysUser user);

    /**
     * 跟据id删除项目信息
     *
     * @param id   项目id
     * @param user 登录用户
     */
    void deleteRelocationProject(Long id, SysUser user);

    /**
     * 获取所有合同编号
     */
    List<String> getContractNumList();

    /**
     * 每隔一个月掉用一次该接口修改未全额回款合同历时
     */
    void updateContractDuration();

    /**
     * 判断表名是否正确
     */
    void judgeFileName(String fileName);

}
