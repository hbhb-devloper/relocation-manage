package com.hbhb.cw.relocation.service.impl;


import com.hbhb.core.bean.BeanConverter;
import com.hbhb.cw.relocation.mapper.FileMapper;
import com.hbhb.cw.relocation.mapper.ProjectMapper;
import com.hbhb.cw.relocation.mapper.WarnMapper;
import com.hbhb.cw.relocation.model.RelocationFile;
import com.hbhb.cw.relocation.model.RelocationWarn;
import com.hbhb.cw.relocation.service.WarnService;
import com.hbhb.cw.relocation.web.vo.WarnExportVO;
import com.hbhb.cw.relocation.web.vo.WarnFileVO;
import com.hbhb.cw.relocation.web.vo.WarnReqVO;
import com.hbhb.cw.relocation.web.vo.WarnResVO;
import com.hbhb.cw.systemcenter.vo.SelectVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WarnServiceImp implements WarnService {

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private WarnMapper warnMapper;

    @Resource
    private FileMapper fileMapper;

    @Override
    public List<WarnResVO> getWarn(WarnReqVO reqVO) {
        List<WarnResVO> WarnResVOS = warnMapper.selectProjectWarnByCond(reqVO);
        Map<String, String> isReceived = new HashMap<>();
        isReceived.put("1", "是");
        isReceived.put("2", "否");
        WarnResVOS.forEach(item -> item.setIsReceived(isReceived.get(item.getIsReceived())));
        return WarnResVOS;
    }

    @Override
    public List<WarnExportVO> export(WarnReqVO reqVO) {
        List<WarnResVO> list = warnMapper.selectProjectWarnByCond(reqVO);
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
            List<SelectVO> selectVoList = new ArrayList<>();
            for (String project : projectNum) {
                SelectVO selectVO = new SelectVO();
                selectVO.setId(Long.valueOf(1));
                selectVO.setLabel(project);
                selectVoList.add(selectVO);
            }
            if(projectNum.size()!=0) {
                warnMapper.updateSateByProjectNum(selectVoList);
            }
        }
        // 新增预警信息
        List<WarnResVO> WarnResVO = projectMapper.selectProjectWarn();
        List<RelocationWarn> list = new ArrayList<>();

        WarnResVO.forEach(item->list.add(RelocationWarn.builder()
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
                .build()));
        // 每隔一个月执行一次api向预警信息表里提供一次数据
        warnMapper.insertBatch(list);
    }

    @Override
    public void addWarnFile(WarnFileVO fileVO) {
        RelocationFile file = new RelocationFile();
        BeanUtils.copyProperties(fileVO, file);
        fileMapper.insert(file);
    }
}
