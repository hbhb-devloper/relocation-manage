package com.hbhb.cw.relocation.service.impl;


import com.hbhb.core.bean.BeanConverter;
import com.hbhb.cw.relocation.enums.IsReceived;
import com.hbhb.cw.relocation.mapper.FileMapper;
import com.hbhb.cw.relocation.mapper.ProjectMapper;
import com.hbhb.cw.relocation.mapper.WarnMapper;
import com.hbhb.cw.relocation.model.RelocationFile;
import com.hbhb.cw.relocation.model.RelocationWarn;
import com.hbhb.cw.relocation.rpc.FileApiExp;
import com.hbhb.cw.relocation.rpc.FlowApiExp;
import com.hbhb.cw.relocation.rpc.UnitApiExp;
import com.hbhb.cw.relocation.rpc.UserApiExp;
import com.hbhb.cw.relocation.service.MailService;
import com.hbhb.cw.relocation.service.WarnService;
import com.hbhb.cw.relocation.web.vo.WarnCountVO;
import com.hbhb.cw.relocation.web.vo.WarnExportVO;
import com.hbhb.cw.relocation.web.vo.WarnFileResVO;
import com.hbhb.cw.relocation.web.vo.WarnReqVO;
import com.hbhb.cw.relocation.web.vo.WarnResVO;
import com.hbhb.cw.systemcenter.model.File;
import com.hbhb.cw.systemcenter.vo.UserInfo;

import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import static java.lang.Integer.parseInt;

/**
 * @author wangxiaogang
 */
@Service
@Slf4j
public class WarnServiceImpl implements WarnService {

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private WarnMapper warnMapper;

    @Resource
    private FileMapper fileMapper;

    @Resource
    private FileApiExp fileApiExp;

    @Resource
    private UnitApiExp unitApi;

    @Resource
    private UserApiExp userApi;

    @Resource
    private FlowApiExp flowApi;

    @Resource
    private MailService mailService;


    @Override
    public List<WarnResVO> getWarn(WarnReqVO reqVO, Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        if (userApi.isAdmin(userId)) {
            reqVO.setUnitId(null);
        } else {
            reqVO.setUnitId(user.getUnitId());
        }
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        // 组装单位，状态
        List<WarnResVO> warnResVo = warnMapper.selectProjectWarnByCond(reqVO);
        Map<Integer, String> isReceived = getIsReceived();
        warnResVo.forEach(item -> {
            item.setIsReceived(isReceived.get(parseInt(item.getIsReceived())));
            item.setUnitName(unitMap.get(item.getUnitId()));
        });
        return warnResVo;
    }

    @Override
    public List<WarnExportVO> export(WarnReqVO cond) {
        List<WarnResVO> list = warnMapper.selectProjectWarnByCond(cond);
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        Map<Integer, String> isReceived = getIsReceived();
        list.forEach(item -> {
            item.setUnitName(unitMap.get(item.getUnitId()));
            item.setIsReceived(isReceived.get(parseInt(item.getIsReceived())));
        });
        return BeanConverter.copyBeanList(list, WarnExportVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSaveWarn() {
        // 更新预警状态
        // 1.查询预警表所有状态为正常的预警的项目编号
        List<String> projectNumWarn = warnMapper.selectProjectNum();
        // 2.跟据项目编号查询基础表中赔补状态变更为全额回款的项目
        if (projectNumWarn.size() != 0) {
            List<String> projectNum = projectMapper.selectProjectNumByProjectNum(projectNumWarn);
            // 3.跟据查寻到的已全额回款的项目编号更新预警信息
            if (projectNum.size() != 0) {
                warnMapper.updateSateByProjectNum(projectNum);
            }
        }
        List<RelocationWarn> list = new ArrayList<>();
        // 开票未回款预警
        List<WarnResVO> warnResVoList = projectMapper.selectProjectStartWarn();
        warnResVoList.forEach(item -> list.add(RelocationWarn.builder()
                .projectId(item.getProjectId())
                .projectNum(item.getProjectNum())
                .anticipatePayment(new BigDecimal(item.getAnticipatePayment()))
                .constructionUnit(item.getConstructionUnit())
                .contractDuration(item.getContractDuration())
                .contractNum(item.getContractNum())
                .finalPayment(new BigDecimal(item.getFinalPayment()))
                .oppositeUnit(item.getOppositeUnit())
                .unitId(item.getUnitId())
                .isReceived(false)
                .state(true)
                .compensationSate(item.getCompensationSate())
                .type(0)
                .build()));
        // 新增预警信息 1-合同到期未回款预警
        List<WarnResVO> warnResVO = projectMapper.selectProjectFinalWarn();
        warnResVO.forEach(item -> list.add(RelocationWarn.builder()
                .projectId(item.getProjectId())
                .projectNum(item.getProjectNum())
                .anticipatePayment(new BigDecimal(item.getAnticipatePayment()))
                .constructionUnit(item.getConstructionUnit())
                .contractDuration(item.getContractDuration())
                .contractNum(item.getContractNum())
                .finalPayment(new BigDecimal(item.getFinalPayment()))
                .oppositeUnit(item.getOppositeUnit())
                .unitId(item.getUnitId())
                .isReceived(false)
                .state(true)
                .compensationSate(item.getCompensationSate())
                .type(1)
                .build()));
        // 每隔一个月执行一次api向预警信息表里提供一次数据
        warnMapper.insertBatch(list);
        // todo 完善推送  分两种情况开票未回款预警、合同到期未回款预警
        // 1.按照单位进行统计开票未回款预警、
        List<WarnCountVO> warnList = projectMapper.selectProjectWarnCount();
        Map<Integer, Integer> warnMap = warnList.stream().collect(Collectors.toMap(WarnCountVO::getUnitId, WarnCountVO::getCount));
        // 2.按照统计数据向每个单位负责人推送邮件信息
        List<Integer> userIdList = flowApi.getFlowRoleUserList("迁改预警负责人");
        List<UserInfo> userList = userApi.getUserInfoBatch(userIdList);
        Set<Integer> keys = warnMap.keySet();
        for (Integer unitId : keys) {
            // 该单位对应的条数
            Integer count = warnMap.get(unitId);
            // 向每个单位负责人推送邮件
            for (UserInfo userVO : userList) {
                if (unitId.equals(userVO.getUnitId())) {
                    mailService.postMail("1515689038@qq.com", userVO.getNickName(), count.toString());
                }
            }
        }
        // 3.按照单位进行统计合同到期未回款预警

    }

    @Override
    public void addWarnFile(RelocationFile relocationFile) {
        fileMapper.insert(relocationFile);
    }

    @Override
    public int getWarnCount(Integer unitId) {
        return warnMapper.selectWarnCountByUnitId(unitId);
    }

    @Override
    public List<WarnFileResVO> getWarnFileList(Long warnId) {
        List<Integer> list = fileMapper.selectFileByWarnId(warnId);
        if (list.size() > 0) {
            List<File> fileList = fileApiExp.getFileInfoBatch(list);
            List<WarnFileResVO> fileVo = new ArrayList<>();
            fileList.forEach(item -> fileVo.add(WarnFileResVO.builder()
                    .fileId(item.getId())
                    .fileName(item.getFileName())
                    .filepath(item.getFilePath())
                    .build()));
            return fileVo;
        }
        return null;
    }

    @Override
    public PageResult<WarnResVO> getWarnList(WarnReqVO cond, Integer userId, Integer pageNum, Integer pageSize) {
        // 判断该用户是否具有流程角色
        List<Integer> userIds = flowApi.getFlowRoleUserList("迁改预警负责人");
        if (userIds.contains(userId)) {
            Map<Integer, String> unitMap = unitApi.getUnitMapById();
            UserInfo userById = userApi.getUserInfoById(userId);
            cond.setUnitId(userById.getUnitId());
            PageRequest<WarnResVO> request = DefaultPageRequest.of(pageNum, pageSize);
            PageResult<WarnResVO> warnResVo = warnMapper.selectWarnListByCond(cond, request);
            Map<Integer, String> isReceived = getIsReceived();
            warnResVo.getList().forEach(item -> {
                item.setIsReceived(isReceived.get(parseInt(item.getIsReceived())));
                item.setUnitName(unitMap.get(item.getUnitId()));
            });
            return warnResVo;
        }
        return null;
    }

    private Map<Integer, String> getIsReceived() {
        Map<Integer, String> receivedMap = new HashMap<>();
        receivedMap.put(IsReceived.RECEIVED.key(), IsReceived.RECEIVED.value());
        receivedMap.put(IsReceived.NOT_RECEIVED.key(), IsReceived.NOT_RECEIVED.value());
        receivedMap.put(IsReceived.PART_RECEIVED.key(), IsReceived.RECEIVED.value());
        return receivedMap;
    }

}
