package com.hbhb.cw.relocation.service.impl;

import com.hbhb.core.bean.BeanConverter;
import com.hbhb.cw.relocation.mapper.ProjectMapper;
import com.hbhb.cw.relocation.service.StatementService;
import com.hbhb.cw.relocation.web.vo.ProjectResVO;
import com.hbhb.cw.relocation.web.vo.StatementExportVO;
import com.hbhb.cw.relocation.web.vo.StatementResVO;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class StatementServiceImpl implements StatementService {
    @Resource
    private ProjectMapper projectMapper;
    @Value("${cw.unit-id.hangzhou}")
    private Integer hangzhou;

    @Override
    public PageResult<StatementResVO> getStatementList(Integer pageNum, Integer pageSize, Integer unitId) {
        if (unitId.equals(hangzhou)) {
            unitId = null;
        }
        PageRequest<ProjectResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        return projectMapper.selectProjectStatementByUnitId(unitId,request);
    }

    @Override
    public List<StatementExportVO> export(Integer unitId) {
        if (unitId.equals(hangzhou)) {
            unitId = null;
        }
        Integer pageNum = 1;
        Integer pageSize =20;
        PageRequest<ProjectResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<StatementResVO> statement = projectMapper.selectProjectStatementByUnitId(unitId,request);
        return BeanConverter.copyBeanList(statement.getList(), StatementExportVO.class);
    }
}
