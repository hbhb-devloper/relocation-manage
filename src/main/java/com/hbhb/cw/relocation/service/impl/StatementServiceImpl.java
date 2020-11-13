package com.hbhb.cw.relocation.service.impl;

import com.hbhb.core.bean.BeanConverter;
import com.hbhb.cw.relocation.mapper.ProjectMapper;
import com.hbhb.cw.relocation.rpc.UnitApiExp;
import com.hbhb.cw.relocation.service.StatementService;
import com.hbhb.cw.relocation.web.vo.ProjectResVO;
import com.hbhb.cw.relocation.web.vo.StatementExportVO;
import com.hbhb.cw.relocation.web.vo.StatementResVO;
import com.hbhb.cw.systemcenter.model.Unit;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wangxiaogang
 */
@Service
@Slf4j
public class StatementServiceImpl implements StatementService {
    @Resource
    private ProjectMapper projectMapper;
    @Value("${cw.unit-id.hangzhou}")
    private Integer hangzhou;

    @Resource
    private UnitApiExp unitApi;

    @Override
    public PageResult<StatementResVO> getStatementList(Integer pageNum, Integer pageSize, Integer unitId) {
        if (unitId.equals(hangzhou)) {
            unitId = null;
        }
        PageRequest<ProjectResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<StatementResVO> statementResVO = projectMapper.selectProjectStatementByUnitId(unitId, request);
        List<Unit> unitList = unitApi.getAllUnitList();
        Map<Integer,String> unitMap = unitList.stream().collect(Collectors.toMap(Unit::getId,Unit::getUnitName));
        statementResVO.getList().forEach(item->item.setUnitName(unitMap.get(item.getUnitId())));
        return statementResVO;
    }

    @Override
    public List<StatementExportVO> export(Integer unitId) {
        if (unitId.equals(hangzhou)) {
            unitId = null;
        }
        List<StatementResVO> statementList = projectMapper.selectProjectStatementListByUnitId(unitId);
        List<Unit> unitList = unitApi.getAllUnitList();
        Map<Integer,String> unitMap = unitList.stream().collect(Collectors.toMap(Unit::getId,Unit::getUnitName));
        statementList.forEach(item->item.setUnitName(unitMap.get(item.getUnitId())));
        return BeanConverter.copyBeanList(statementList, StatementExportVO.class);
    }
}
