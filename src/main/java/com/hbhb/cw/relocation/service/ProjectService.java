package com.hbhb.cw.relocation.service;

import com.hbhb.cw.relocation.web.vo.ProjectImportVO;
import com.hbhb.cw.relocation.web.vo.ProjectReqVO;
import com.hbhb.cw.relocation.web.vo.ProjectResVO;
import com.hbhb.cw.systemcenter.model.SysUser;
import com.hbhb.cw.systemcenter.vo.FileVO;
import org.beetl.sql.core.page.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @author wangxiaogang
 */
public interface ProjectService {
    /**
     * 批量导入传输迁改信息
     *
     * @param importVOList 迁改信息集合
     */
    void addSaveRelocationProject(List<ProjectImportVO> importVOList, Map<Integer, String> importHeadMap) throws ParseException;

    /**
     * 跟据条件分页查询迁改台账列表
     *
     * @param cond     条件
     * @param pageNum  页数
     * @param pageSize 条数
     * @return 列表
     */
    PageResult<ProjectResVO> getRelocationProjectList(ProjectReqVO cond, Integer pageNum, Integer pageSize, Integer userId);

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
     * @param id     项目id
     * @param userId 登录用户
     */
    void deleteRelocationProject(Long id, Integer userId);

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

    /**
     * 批量删除基础信息
     *
     * @param ids ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 跟据id获取迁改信息详情
     *
     * @param id 项目id
     * @return 项目信息详情
     */
    ProjectResVO getProject(Long id);

    /**
     * 判断上传附件是否与合同编号对应
     *
     * @param files 附件
     * @return 是否包含
     */
    Boolean judgeContractNum(MultipartFile files);

    /**
     * 修改合同文件id
     *
     * @param file 文件信息
     */
    void updateContractFileId(FileVO file);

    /**
     * 导入返回结果集
     *
     * @return 结果
     */
    List<String> getMsg();


}
