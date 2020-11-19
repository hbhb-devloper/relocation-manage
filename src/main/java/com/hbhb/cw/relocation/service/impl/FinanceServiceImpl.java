package com.hbhb.cw.relocation.service.impl;


import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.IsReceived;
import com.hbhb.cw.relocation.mapper.FinanceMapper;
import com.hbhb.cw.relocation.rpc.SysUserApiExp;
import com.hbhb.cw.relocation.rpc.UnitApiExp;
import com.hbhb.cw.relocation.service.FinanceService;
import com.hbhb.cw.relocation.web.vo.FinanceReqVO;
import com.hbhb.cw.relocation.web.vo.FinanceResVO;
import com.hbhb.cw.systemcenter.model.Unit;
import com.hbhb.cw.systemcenter.vo.SysUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hyk
 * @since 2020-10-9
 */
@Service
@Slf4j
public class FinanceServiceImpl implements FinanceService {

    @Resource
    private FinanceMapper financeMapper;

    @Resource
    private SysUserApiExp sysUserApiExp;

    @Resource
    private UnitApiExp unitApi;

    @Override
    public PageResult<FinanceResVO> getFinanceList(Integer pageNum, Integer pageSize,
                                                   FinanceReqVO cond, Integer userId) throws UnsupportedEncodingException {
        if (cond.getContractNum() != null) {
            String s = URLDecoder.decode(cond.getContractNum(), "UTF-8");
            cond.setContractNum(s);
        }
        String currentYear = DateUtil.getCurrentYear();
        if (StringUtils.isEmpty(cond.getYear())) {
            cond.setYear(currentYear);
        }
        PageRequest<FinanceResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        setUnitId(cond, userId);
        PageResult<FinanceResVO> financeResVos = financeMapper.getFinanceList(cond, request);
        Map<String, String> isReceived = getIsReceived();

        List<Unit> unitList = unitApi.getAllUnitList();
        Map<Integer, String> unitMap = unitList.stream().collect(Collectors.toMap(Unit::getId, Unit::getUnitName));

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
        Map<String, String> isReceived = getIsReceived();
        financeResVos.forEach(item -> {
            //网银打款、现金转账，开具发票收据
            item.setPayType("网银打款");
            item.setIsAllReceived(isReceived.get(item.getIsAllReceived()));
        });
        return financeResVos;
    }

    private void setUnitId(FinanceReqVO cond, Integer userId) {
        SysUserInfo user = sysUserApiExp.getUserById(userId);
        if (!"admin".equals(user.getUserName())) {
            cond.setUnitId(user.getUnitId());
        }
    }


    private Map<String, String> getIsReceived() {
        Map<String, String> receivedMap = new HashMap<>();
        receivedMap.put(IsReceived.RECEIVED_CODE.value(), IsReceived.RECEIVED.value());
        receivedMap.put(IsReceived.NOT_RECEIVED_CODE.value(), IsReceived.NOT_RECEIVED.value());
        return receivedMap;
    }
}
