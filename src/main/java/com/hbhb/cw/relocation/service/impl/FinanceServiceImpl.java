package com.hbhb.cw.relocation.service.impl;


import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.State;
import com.hbhb.cw.relocation.mapper.FinanceMapper;
import com.hbhb.cw.relocation.rpc.UnitApiExp;
import com.hbhb.cw.relocation.rpc.UserApiExp;
import com.hbhb.cw.relocation.service.FinanceService;
import com.hbhb.cw.relocation.web.vo.FinanceReqVO;
import com.hbhb.cw.relocation.web.vo.FinanceResVO;
import com.hbhb.cw.systemcenter.enums.UnitEnum;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author hyk
 * @since 2020-10-9
 */
@Service
@Slf4j
@SuppressWarnings(value = {"unchecked"})
public class FinanceServiceImpl implements FinanceService {

    @Resource
    private FinanceMapper financeMapper;

    @Resource
    private UserApiExp userApi;

    @Resource
    private UnitApiExp unitApi;

    @Override
    public PageResult<FinanceResVO> getFinanceList(Integer pageNum, Integer pageSize,
                                                   FinanceReqVO cond, Integer userId) {
        String currentYear = DateUtil.getCurrentYear();
        if (StringUtils.isEmpty(cond.getYear())) {
            cond.setYear(currentYear);
        }
        PageRequest<FinanceResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        setUnitId(cond, userId);

        List<Integer> unitIds = new ArrayList<>();
        if (UnitEnum.isBenbu(cond.getUnitId())) {
            unitIds = unitApi.getSubUnit(cond.getUnitId());
        }
        cond.setUnitIds(unitIds);

        PageResult<FinanceResVO> financeResVos = financeMapper.getFinanceList(cond, request);
        Map<String, String> isReceived = getIsAllReceived();
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        // 组装理赔方式、收款状态、县市
        financeResVos.getList().forEach(item -> {
            //网银打款、现金转账，开具发票收据
            item.setPayType("网银打款");
            item.setIsAllReceived(isReceived.get(item.getIsAllReceived()));
            item.setUnit(unitMap.get(item.getUnitId()));
        });

        return financeResVos;
    }

    @Override
    public List<FinanceResVO> selectExportListByCondition(FinanceReqVO cond, Integer userId) {
        setUnitId(cond, userId);
        String currentYear = DateUtil.getCurrentYear();
        if (StringUtils.isEmpty(cond.getYear())) {
            cond.setYear(currentYear);
        }
        List<FinanceResVO> financeResVos = financeMapper.getFinanceList(cond);
        Map<String, String> isReceived = getIsAllReceived();
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        financeResVos.forEach(item -> {
            //网银打款、现金转账，开具发票收据
            item.setPayType("网银打款");
            item.setIsAllReceived(isReceived.get(item.getIsAllReceived()));
            item.setUnit(unitMap.get(item.getUnitId()));
            item.setCurrentYear(DateUtil.dateToStringY(new Date()));
        });
        return financeResVos;
    }

    private void setUnitId(FinanceReqVO cond, Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        if (cond.getUnitId() == null && userApi.isAdmin(userId)) {
            cond.setUnitId(user.getUnitId());
        }
    }


    private Map<String, String> getIsAllReceived() {
        Map<String, String> receivedMap = new HashMap<>(100);
        receivedMap.put(State.ONE.value(), State.YES.value());
        receivedMap.put(State.ZERO.value(), State.NO.value());
        return receivedMap;
    }
}
