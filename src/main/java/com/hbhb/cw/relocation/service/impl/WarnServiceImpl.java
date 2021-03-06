package com.hbhb.cw.relocation.service.impl;


import com.hbhb.core.bean.BeanConverter;
import com.hbhb.cw.relocation.enums.IsReceived;
import com.hbhb.cw.relocation.enums.WarnType;
import com.hbhb.cw.relocation.mapper.FileMapper;
import com.hbhb.cw.relocation.mapper.ProjectMapper;
import com.hbhb.cw.relocation.mapper.WarnMapper;
import com.hbhb.cw.relocation.model.RelocationFile;
import com.hbhb.cw.relocation.model.RelocationWarn;
import com.hbhb.cw.relocation.rpc.FileApiExp;
import com.hbhb.cw.relocation.rpc.FlowRoleUserApiExp;
import com.hbhb.cw.relocation.rpc.UnitApiExp;
import com.hbhb.cw.relocation.rpc.UserApiExp;
import com.hbhb.cw.relocation.service.MailService;
import com.hbhb.cw.relocation.service.WarnService;
import com.hbhb.cw.relocation.web.vo.WarnCountVO;
import com.hbhb.cw.relocation.web.vo.WarnExportVO;
import com.hbhb.cw.relocation.web.vo.WarnFileResVO;
import com.hbhb.cw.relocation.web.vo.WarnReqVO;
import com.hbhb.cw.relocation.web.vo.WarnResVO;
import com.hbhb.cw.systemcenter.model.SysFile;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

/**
 * @author wangxiaogang
 */
@Service
@Slf4j
@SuppressWarnings(value = {"unchecked"})
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
    private FlowRoleUserApiExp flowApi;

    @Resource
    private MailService mailService;


    @Override
    public PageResult<WarnResVO> getWarn(WarnReqVO reqVO, Integer userId, Integer pageNum, Integer pageSize) {
        UserInfo user = userApi.getUserInfoById(userId);
        if (userApi.isAdmin(userId)) {
            reqVO.setUnitId(null);
        } else {
            reqVO.setUnitId(user.getUnitId());
        }
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        PageRequest<WarnResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        // ?????????????????????
        PageResult<WarnResVO> warnResVo = warnMapper.selectProjectWarnByCond(request, reqVO);
        Map<Integer, String> isReceived = getIsReceived();
        warnResVo.getList().forEach(item -> {
            item.setIsReceived(isReceived.get(parseInt(item.getIsReceived())));
            item.setUnitName(unitMap.get(item.getUnitId()));
        });
        return warnResVo;
    }

    @Override
    public List<WarnExportVO> export(WarnReqVO cond, Integer userId) {
        PageResult<WarnResVO> list = this.getWarn(cond, userId, 1, Integer.MAX_VALUE);
        return BeanConverter.copyBeanList(list.getList(), WarnExportVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSaveWarn() {
        // ??????????????????
        // 1.????????????????????????????????????????????????????????????
        List<String> projectNumWarn = warnMapper.selectProjectNum();
        // 2.??????????????????????????????????????????????????????????????????????????????
        if (projectNumWarn.size() != 0) {
            List<String> projectNum = projectMapper.selectProjectNumByProjectNum(projectNumWarn);
            // 3.??????????????????????????????????????????????????????????????????
            if (projectNum.size() != 0) {
                warnMapper.updateSateByProjectNum(projectNum);
            }
        }
        List<RelocationWarn> list = new ArrayList<>();
        // ?????????????????????
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
                .type(WarnType.START_WARN.value())
                .build()));
        // ?????????????????? 1-???????????????????????????
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
                .type(WarnType.FINAL_WARN.value())
                .build()));
        // ???????????????????????????api???????????????????????????????????????
        warnMapper.insertBatch(list);
        // 1.????????????????????????????????????????????????
        List<WarnCountVO> warnStartList = projectMapper.selectProjectStartWarnCount();
        Map<Integer, Integer> warnStartMap = warnStartList.stream().collect(Collectors.toMap(WarnCountVO::getUnitId, WarnCountVO::getCount));
        // 2.????????????????????????????????????????????????????????????
        List<Integer> userIdList = flowApi.getUserIdByRoleName("?????????????????????");
        if (userIdList.size() != 0) {
            List<UserInfo> userList = userApi.getUserInfoBatch(userIdList);
            Set<Integer> keys = warnStartMap.keySet();
            for (Integer unitId : keys) {
                // ????????????????????????
                Integer count = warnStartMap.get(unitId);
                String context = count + "??????????????????";
                // ????????????????????????????????????
                for (UserInfo userVO : userList) {
                    if (unitId.equals(userVO.getUnitId())) {
                        mailService.postMail(userVO.getEmail(), userVO.getNickName(), context);
                    }
                }
            }
            // 3.???????????????????????????????????????????????????
            List<WarnCountVO> warnFinalList = projectMapper.selectProjectFinalWarnCount();
            Map<Integer, Integer> warnMap = warnFinalList.stream().collect(Collectors.toMap(WarnCountVO::getUnitId, WarnCountVO::getCount));
            Set<Integer> key = warnStartMap.keySet();
            for (Integer unitId : key) {
                // ????????????????????????
                Integer count = warnMap.get(unitId);
                String context = count + "????????????????????????";
                // ????????????????????????????????????
                for (UserInfo userVO : userList) {
                    if (unitId.equals(userVO.getUnitId())) {
                        mailService.postMail(userVO.getEmail(), userVO.getNickName(), context);
                    }
                }
            }
        }
    }

    @Override
    public void addWarnFile(RelocationFile relocationFile) {
        fileMapper.insert(relocationFile);
    }

    @Override
    public Long getWarnCount(Integer unitId) {
        return warnMapper.selectWarnCountByUnitId(unitId);
    }

    @Override
    public List<WarnFileResVO> getWarnFileList(Long warnId) {
        List<Integer> list = fileMapper.selectFileByWarnId(warnId);
        if (list.size() > 0) {
            List<SysFile> fileList = fileApiExp.getFileInfoBatch(list);
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
        // ???????????????????????????????????????
        List<Integer> userIds = flowApi.getUserIdByRoleName("?????????????????????");
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
        Map<Integer, String> receivedMap = new HashMap<>(100);
        receivedMap.put(IsReceived.RECEIVED.key(), IsReceived.RECEIVED.value());
        receivedMap.put(IsReceived.NOT_RECEIVED.key(), IsReceived.NOT_RECEIVED.value());
        receivedMap.put(IsReceived.PART_RECEIVED.key(), IsReceived.RECEIVED.value());
        return receivedMap;
    }

}
