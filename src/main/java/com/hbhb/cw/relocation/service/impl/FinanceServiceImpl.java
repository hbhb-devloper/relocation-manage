package com.hbhb.cw.relocation.service.impl;


import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.State;
import com.hbhb.cw.relocation.mapper.FinanceMapper;
import com.hbhb.cw.relocation.mapper.ProjectMapper;
import com.hbhb.cw.relocation.rpc.UnitApiExp;
import com.hbhb.cw.relocation.rpc.UserApiExp;
import com.hbhb.cw.relocation.service.FinanceService;
import com.hbhb.cw.relocation.web.vo.FinanceReqVO;
import com.hbhb.cw.relocation.web.vo.FinanceResVO;
import com.hbhb.cw.relocation.web.vo.FinanceStatisticsVO;
import com.hbhb.cw.relocation.web.vo.ProjectSelectVO;
import com.hbhb.cw.systemcenter.enums.UnitEnum;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alibaba.excel.util.StringUtils.isEmpty;


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
    private ProjectMapper projectMapper;

    @Resource
    private UserApiExp userApi;

    @Resource
    private UnitApiExp unitApi;

    @Override
    public PageResult<FinanceResVO> getFinanceList(Integer pageNum, Integer pageSize,
                                                   FinanceReqVO cond, Integer userId) {
        // 设置年份
        String currentYear = DateUtil.getCurrentYear();
        if (StringUtils.isEmpty(cond.getYear())) {
            cond.setYear(currentYear);
        }

        // 设置默认查询单位
        setUnitId(cond, userId);
        List<Integer> unitIds = new ArrayList<>();
        if (UnitEnum.isBenbu(cond.getUnitId())) {
            unitIds = unitApi.getSubUnit(cond.getUnitId());
        }
        cond.setUnitIds(unitIds);
        PageRequest<FinanceResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<FinanceResVO> financeResVos = financeMapper.getFinanceList(cond, request);
        getFinanceList(financeResVos.getList(), cond.getYear());
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
        getFinanceList(financeResVos, cond.getYear());
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

    private void getFinanceList(List<FinanceResVO> financeList, String year) {
        // 按合同纬度统计每月份收款信息
        List<FinanceStatisticsVO> statistics = financeMapper.selectSumPayMonthAmount(year);
        Map<String, FinanceStatisticsVO> contractMap = statistics.stream()
                .collect(Collectors.toMap(FinanceStatisticsVO::getContractNum, Function.identity()));
        List<String> contractNumList = new ArrayList<>();
        statistics.forEach(item -> contractNumList.add(item.getContractNum()));


        // 获取按合同划分预算统计
        List<String> list = projectMapper.selectContractNumList();
        List<ProjectSelectVO> totalList = projectMapper.selectSumConstructionBudget(list);
        Map<String, BigDecimal> contractBudgetMap = totalList.stream()
                .collect(Collectors.toMap(ProjectSelectVO::getNum, ProjectSelectVO::getConstructionBudget));

        // 收款状态
        Map<String, String> isReceived = getIsAllReceived();
        // 单位
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        BigDecimal zero = BigDecimal.ZERO;
        for (FinanceResVO item : financeList) {
            item.setPayType("网银打款");
            // 预付款是否完全到账
            item.setIsAllReceived(isReceived.get(item.getIsAllReceived()));
            // 单位
            item.setUnit(unitMap.get(item.getUnitId()));
            if (!isEmpty(item.getContractNum()) && contractNumList.contains(item.getContractNum())) {
                // 1月回款
                item.setJanReceivable((item.getConstructionBudget()
                        .divide(contractBudgetMap.get(item.getContractNum()), 4, 4))
                        .multiply(contractMap.get(item.getContractNum()).getJanReceivable())
                );
                // 2月回款
                item.setFebReceivable((item.getConstructionBudget()
                        .divide(contractBudgetMap.get(item.getContractNum()), 4, 4))
                        .multiply(contractMap.get(item.getContractNum()).getFebReceivable())
                );
                // 3月回款
                item.setMarReceivable((item.getConstructionBudget()
                        .divide(contractBudgetMap.get(item.getContractNum()), 4, 4))
                        .multiply(contractMap.get(item.getContractNum()).getFebReceivable())
                );
                // 4月回款
                item.setAprReceivable((item.getConstructionBudget()
                        .divide(contractBudgetMap.get(item.getContractNum()), 4, 4))
                        .multiply(contractMap.get(item.getContractNum()).getAprReceivable())
                );
                // 5月回款
                item.setMayReceivable((item.getConstructionBudget()
                        .divide(contractBudgetMap.get(item.getContractNum()), 4, 4))
                        .multiply(contractMap.get(item.getContractNum()).getMayReceivable())
                );
                // 6月回款
                item.setJuneReceivable((item.getConstructionBudget()
                        .divide(contractBudgetMap.get(item.getContractNum()), 4, 4))
                        .multiply(contractMap.get(item.getContractNum()).getJuneReceivable())
                );
                // 7月回款
                item.setJulReceivable((item.getConstructionBudget()
                        .divide(contractBudgetMap.get(item.getContractNum()), 4, 4))
                        .multiply(contractMap.get(item.getContractNum()).getJulReceivable())
                );
                // 8月回款
                item.setAugReceivable((item.getConstructionBudget()
                        .divide(contractBudgetMap.get(item.getContractNum()), 4, 4))
                        .multiply(contractMap.get(item.getContractNum()).getAugReceivable())
                );
                // 9月回款
                item.setSepReceivable((item.getConstructionBudget()
                        .divide(contractBudgetMap.get(item.getContractNum()), 4, 4))
                        .multiply(contractMap.get(item.getContractNum()).getSepReceivable())
                );
                // 10月回款
                item.setOctReceivable((item.getConstructionBudget()
                        .divide(contractBudgetMap.get(item.getContractNum()), 4, 4))
                        .multiply(contractMap.get(item.getContractNum()).getOctReceivable())
                );
                // 11月回款
                item.setNovReceivable((item.getConstructionBudget()
                        .divide(contractBudgetMap.get(item.getContractNum()), 4, 4))
                        .multiply(contractMap.get(item.getContractNum()).getNovReceivable())
                );
                // 12月回款
                item.setDecReceivable((item.getConstructionBudget()
                        .divide(contractBudgetMap.get(item.getContractNum()), 4, 4))
                        .multiply(contractMap.get(item.getContractNum()).getDecReceivable())
                );
                // 初始化回收金额
                item.setInitRecoveredAmount((item.getConstructionBudget()
                        .divide(contractBudgetMap.get(item.getContractNum()), 4, 4))
                        .multiply(contractMap.get(item.getContractNum()).getInitRecoveredAmount())
                );
                // 已开票金额
                item.setInvoicedAmount((item.getConstructionBudget()
                        .divide(contractBudgetMap.get(item.getContractNum()), 4, 4))
                        .multiply(contractMap.get(item.getContractNum()).getInvoicedAmount())
                );
            } else {
                item.setJanReceivable(zero);
                item.setFebReceivable(zero);
                item.setMarReceivable(zero);
                item.setAprReceivable(zero);
                item.setMayReceivable(zero);
                item.setJuneReceivable(zero);
                item.setJulReceivable(zero);
                item.setAugReceivable(zero);
                item.setSepReceivable(zero);
                item.setOctReceivable(zero);
                item.setNovReceivable(zero);
                item.setDecReceivable(zero);
                item.setInitRecoveredAmount(zero);
                item.setInvoicedAmount(zero);
            }
        }
    }
}