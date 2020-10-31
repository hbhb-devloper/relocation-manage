package com.hbhb.cw.relocation.service.impl;


import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.mapper.FinanceMapper;
import com.hbhb.cw.relocation.rpc.SysUserApiExp;
import com.hbhb.cw.relocation.service.FinanceService;
import com.hbhb.cw.relocation.web.vo.FinanceReqVO;
import com.hbhb.cw.relocation.web.vo.FinanceResVO;
import com.hbhb.cw.systemcenter.vo.SysUserInfo;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    /**
     * 列表分页查询
     *
     * @param pageNum
     * @param pageSize
     * @param cond
     * @param userId
     * @return
     * @throws UnsupportedEncodingException
     */
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
        PageResult<FinanceResVO> financeResVOS = financeMapper
            .getFinanceList(cond, request);
        List<FinanceResVO> list = financeResVOS.getList();
        for (FinanceResVO financeResVO : list) {
            financeResVO.setCurrentYear(cond.getYear());
            //网银打款、现金转账，开具发票收据
            financeResVO.setPayType("网银打款");
            String isAllReceived = financeResVO.getIsAllReceived();
            if ("1".equals(isAllReceived)) {
                financeResVO.setIsAllReceived("是");
            }
            if ("0".equals(isAllReceived)) {
                financeResVO.setIsAllReceived("否");
            }
        }
        return financeResVOS;
    }

    /**
     * 导出
     *
     * @param cond
     * @param userId
     * @return
     */
    @Override
    public List<FinanceResVO> selectExportListByCondition(FinanceReqVO cond,
        Integer userId) {
        setUnitId(cond, userId);
        String currentYear = DateUtil.getCurrentYear();
        if (StringUtils.isEmpty(cond.getYear())) {
            cond.setYear(currentYear);
        }
        List<FinanceResVO> financeResVOS = financeMapper
            .getFinanceList(cond);
        for (FinanceResVO financeResVO : financeResVOS) {
            financeResVO.setCurrentYear(cond.getYear());
            financeResVO.setPayType("网银打款");
            String isAllReceived = financeResVO.getIsAllReceived();
            if ("1".equals(isAllReceived)) {
                financeResVO.setIsAllReceived("是");
            }
            if ("0".equals(isAllReceived)) {
                financeResVO.setIsAllReceived("否");
            }
        }
        return financeResVOS;
    }

    private void setUnitId(FinanceReqVO cond, Integer userId) {
        SysUserInfo user = sysUserApiExp.getUserById(userId);
        if (!"admin".equals(user.getUserName())) {
            cond.setUnitId(user.getUnitId());
        }
    }

}
