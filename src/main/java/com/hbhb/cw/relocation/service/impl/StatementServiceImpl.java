package com.hbhb.cw.relocation.service.impl;

import com.hbhb.core.bean.BeanConverter;
import com.hbhb.cw.relocation.enums.UnitAbbr;
import com.hbhb.cw.relocation.mapper.ProjectMapper;
import com.hbhb.cw.relocation.rpc.UnitApiExp;
import com.hbhb.cw.relocation.rpc.UserApiExp;
import com.hbhb.cw.relocation.service.StatementService;
import com.hbhb.cw.relocation.web.vo.ProjectResVO;
import com.hbhb.cw.relocation.web.vo.StatementExportVO;
import com.hbhb.cw.relocation.web.vo.StatementResVO;
import com.hbhb.cw.systemcenter.enums.UnitEnum;
import com.hbhb.cw.systemcenter.model.Unit;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author wangxiaogang
 */
@Service
@Slf4j
@SuppressWarnings(value = {"unchecked"})
public class StatementServiceImpl implements StatementService {
    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private UnitApiExp unitApi;
    @Resource
    private UserApiExp userApi;

    @Override
    public PageResult<StatementResVO> getStatementList(Integer pageNum, Integer pageSize, Integer unitId, Integer userId) {
        if (UnitEnum.isHangzhou(unitId)) {
            unitId = null;
        }
        UserInfo user = userApi.getUserInfoById(userId);
        Unit unitInfo = unitApi.getUnitInfo(user.getUnitId());
        if (UnitAbbr.CWB.value().equals(unitInfo.getUnitName())
                || UnitAbbr.WLB.value().equals(unitInfo.getUnitName())) {
            unitId = null;
        }
        PageRequest<ProjectResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<StatementResVO> statementResVO = projectMapper.selectProjectStatementByUnitId(unitId, request);
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        statementResVO.getList().forEach(item -> item.setUnitName(unitMap.get(item.getUnitId())));
        return statementResVO;
    }

    @Override
    public List<StatementExportVO> export(Integer unitId) {
        if (UnitEnum.isHangzhou(unitId)) {
            unitId = null;
        }
        List<StatementResVO> statementList = projectMapper.selectProjectStatementListByUnitId(unitId);
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        statementList.forEach(item -> item.setUnitName(unitMap.get(item.getUnitId())));
        return BeanConverter.copyBeanList(statementList, StatementExportVO.class);
    }
}
