package com.hbhb.cw.relocation.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.*;
import com.hbhb.cw.relocation.exception.InvoiceException;
import com.hbhb.cw.relocation.exception.RelocationException;
import com.hbhb.cw.relocation.mapper.IncomeMapper;
import com.hbhb.cw.relocation.mapper.InvoiceMapper;
import com.hbhb.cw.relocation.mapper.ProjectMapper;
import com.hbhb.cw.relocation.model.RelocationIncome;
import com.hbhb.cw.relocation.model.RelocationInvoice;
import com.hbhb.cw.relocation.model.RelocationProject;
import com.hbhb.cw.relocation.rpc.SysUserApiExp;
import com.hbhb.cw.relocation.rpc.UnitApiExp;
import com.hbhb.cw.relocation.service.InvoiceService;
import com.hbhb.cw.relocation.web.vo.*;
import com.hbhb.cw.systemcenter.enums.AllName;
import com.hbhb.cw.systemcenter.model.Unit;
import com.hbhb.cw.systemcenter.vo.ParentVO;
import com.hbhb.cw.systemcenter.vo.SysUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author xiaokang
 * @since 2020-09-22
 */
@Service
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    @Resource
    private InvoiceMapper relocationInvoiceMapper;

    @Resource
    private SysUserApiExp sysUserApiExp;

    @Resource
    private UnitApiExp unitApiExp;

    @Resource
    private IncomeMapper relocationIncomeMapper;

    @Resource
    private ProjectMapper projectMapper;

    @Override
    public PageResult<InvoiceResVO> getInvoiceList(Integer pageNum, Integer pageSize,
                                                   InvoiceReqVO cond, Integer userId) {
        List<Unit> unitList = unitApiExp.getAllUnitList();
        ParentVO parentUnit = unitApiExp.getParentUnit();
        List<Integer> unitIds = new ArrayList<>();
        for (Unit unit : unitList) {
            if (parentUnit.getBenbu().equals(cond.getUnitId())) {
                unitIds.add(unit.getId());
            }
        }
        cond.setUnitIds(unitIds);
        PageRequest<InvoiceResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<InvoiceResVO> invoiceResVo = relocationInvoiceMapper.selectListByCondition(cond, request);
        Map<Integer, String> unitMap = unitList.stream().collect(Collectors.toMap(Unit::getId, Unit::getUnitName));
        invoiceResVo.getList().forEach(item -> {
            item.setInvoiceTypeLabel(State.ONE.value().equals(item.getInvoiceType().toString()) ? InvoiceType.PLAIN_INVOICE.value() : InvoiceType.SPECIAL_INVOICE.value());
            item.setState(State.ONE.value().equals(item.getState()) ? InvoiceSate.BLUE_STATE.value() : InvoiceSate.RED_STATE.value());
            item.setIsImport(State.ONE.value().equals(item.getState()) ? State.YES.value() : State.NO.value());
            item.setUnit(unitMap.get(item.getUnitId()));
            item.setDistrict(unitMap.get(item.getDistrictId()));
        });
        return invoiceResVo;
    }

    @Override
    public RelocationInvoice getInvoiceDetail(Long id) {
        return relocationInvoiceMapper.single(id);
    }

    @Override
    public void updateInvoice(InvoiceResVO invoiceVo) {
        RelocationInvoice invoice = translation(invoiceVo);
        relocationInvoiceMapper.updateById(invoice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addInvoice(InvoiceResVO invoiceVo) {
        RelocationInvoice invoice = translation(invoiceVo);
        relocationInvoiceMapper.insert(invoice);
        RelocationIncome relocationIncome = getRelocationIncome(invoice, invoice.getProjectId(), invoice.getPaymentType());
        relocationIncomeMapper.insert(relocationIncome);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSaveRelocationInvoice(List<InvoiceImportVO> dataList) {
        List<ProjectInfoVO> projectInfo = relocationInvoiceMapper.getProjectInfo();
        Map<String, Long> projectMap = projectInfo.stream().collect(Collectors.toMap(ProjectInfoVO::getInfo, ProjectInfoVO::getId));
        // TODO 发票匹配验证
        // 转换单位
        List<Unit> list = unitApiExp.getAllUnitList();
        Map<String, Integer> unitMap = list.stream().collect(Collectors.toMap(Unit::getUnitName, Unit::getId));
        List<RelocationInvoice> invoiceList = new ArrayList<>();
        List<RelocationIncome> incomeList = new ArrayList<>();
        for (InvoiceImportVO relocationInvoiceImport : dataList) {
            if (!"财务开票".equals(relocationInvoiceImport.getBusinessType())) {
                throw new InvoiceException(InvoiceErrorCode.RELOCATION_INVOICE_IMPORT_BUSTYPE_ERROR);
            }
            String newRemake = relocationInvoiceImport.getRemake();
            Matcher m = Pattern.compile(Pattern.quote("；")).matcher(newRemake);
            int i = 0;
            while (m.find()) {
                i++;
            }
            if (i != 3) {
                throw new InvoiceException(InvoiceErrorCode.RELOCATION_INVOICE_REMAKE_ERROR);
            }
            RelocationInvoice relocationInvoice = new RelocationInvoice();
            relocationInvoice.setUnitId(unitMap.get(relocationInvoiceImport.getUnitId()));
            relocationInvoice.setDistrict(unitMap.get(relocationInvoiceImport.getDistrict()));
            relocationInvoice.setInvoiceCode(relocationInvoiceImport.getInvoiceCode());
            relocationInvoice.setInvoiceNumber(relocationInvoiceImport.getInvoiceNumber());
            String invoiceType = relocationInvoiceImport.getInvoiceType();
            if ("电子增值税普通发票".equals(invoiceType) || "增值税电子普票".equals(invoiceType)
                    || "增值税电子普通发票".equals(invoiceType) || "增值税普票".equals(invoiceType)) {
                relocationInvoice.setInvoiceType(1);
            }
            if ("电子增值税专用发票".equals(invoiceType) || "增值税专用发票".equals(invoiceType)) {
                relocationInvoice.setInvoiceType(0);
            }
            relocationInvoice.setBuyerTax(relocationInvoiceImport.getBuyerTax());
            relocationInvoice.setBuyerName(relocationInvoiceImport.getBuyerName());
            relocationInvoice.setInvoiceProject(relocationInvoiceImport.getInvoiceProject());
            relocationInvoice.setInvoiceTime(DateUtil.string2DateYMD(relocationInvoiceImport.getInvoiceTime()));
            relocationInvoice.setAmount(new BigDecimal(relocationInvoiceImport.getAmount()));
            relocationInvoice.setTaxRate(new BigDecimal(relocationInvoiceImport.getTaxRate() == null ? "0"
                    : relocationInvoiceImport.getTaxRate()));
            relocationInvoice.setTaxAmount(new BigDecimal(relocationInvoiceImport.getTaxAmount() == null ? "0"
                    : relocationInvoiceImport.getTaxAmount()));
            relocationInvoice.setTaxIncludeAmount(new BigDecimal(relocationInvoiceImport.getTaxIncludeAmount()));
            relocationInvoice.setRemake(relocationInvoiceImport.getRemake());
            relocationInvoice.setApplicant(relocationInvoiceImport.getApplicant());
            relocationInvoice.setIssuer(relocationInvoiceImport.getIssuer());
            // 1 蓝字, 0 红字
            relocationInvoice.setState("蓝字".equals(relocationInvoiceImport.getState()) ? 1 : 0);
            // 1 是, 0 否
            relocationInvoice.setIsImport("是".equals(relocationInvoiceImport.getIsImport()) ? 1 : 0);
            relocationInvoice.setBusinessType(relocationInvoiceImport.getBusinessType());
            relocationInvoice.setManager(relocationInvoiceImport.getManager());
            Long pid = null;
            Integer atype = null;
            if (!StringUtils.isEmpty(newRemake) && !"作废".contains(newRemake) && !"发票作废"
                    .equals(newRemake)) {
                String[] split = newRemake.split("；");
                //合同号
                String contractNum = split[0];
                if (contractNum.startsWith("（")) {
                    contractNum = contractNum.substring(contractNum.indexOf("（") + 1, contractNum.lastIndexOf("）"));
                }
                //区县
                String unit = split[1];
                Integer unitId = unitMap.get(unit);
                //款项性质
                String amountType = split[2];
                switch (amountType) {
                    case "尾款":
                        atype = 2;
                        break;
                    case "预付款":
                        atype = 1;
                        break;
                    case "决算款":
                        atype = 3;
                        break;
                    default:
                        break;
                }
                // 项目信息 匹配id
                String pinfo = split[3];
                String key = contractNum + unitId + pinfo;
                // 若发票可以与项目信息匹配则设置项目状态匹配，否则为不匹配
                pid = projectMap.get(key);
                if (pid != null) {
                    relocationInvoice.setProjectState(true);
                } else {
                    relocationInvoice.setProjectState(true);
                }
            }
            relocationInvoice.setPaymentType(atype);
            relocationInvoice.setProjectId(pid);
            invoiceList.add(relocationInvoice);
            //导入发票后 同步添加到收款
            RelocationIncome relocationIncome = getRelocationIncome(relocationInvoice, pid, atype);
            incomeList.add(relocationIncome);
        }

        relocationInvoiceMapper.insertBatch(invoiceList);
        relocationIncomeMapper.insertBatch(incomeList);
    }

    @Override
    public List<InvoiceExportResVO> selectExportListByCondition(InvoiceReqVO vo, Integer userId) {
        setConditionDetail(vo, userId);
        List<Unit> unitList = unitApiExp.getAllUnitList();
        List<InvoiceExportResVO> exportResVos = relocationInvoiceMapper.selectExportListByCondition(vo);
        Map<Integer, String> unitMap = unitList.stream().collect(Collectors.toMap(Unit::getId, Unit::getUnitName));
        exportResVos.forEach(item -> {
            item.setInvoiceType(State.ONE.value().equals(item.getInvoiceType()) ? InvoiceType.PLAIN_INVOICE.value() : InvoiceType.SPECIAL_INVOICE.value());
            item.setState(State.ONE.value().equals(item.getState()) ? InvoiceSate.BLUE_STATE.value() : InvoiceSate.RED_STATE.value());
            item.setIsImport(State.ONE.value().equals(item.getState()) ? State.YES.value() : State.NO.value());
            item.setUnit(unitMap.get(item.getUnitId()));
            item.setDistrict(unitMap.get(item.getDistrictId()));
        });
        return exportResVos;
    }


    private void setConditionDetail(InvoiceReqVO cond, Integer userId) {
        SysUserInfo user = sysUserApiExp.getUserById(userId);
        if (!"admin".equals(user.getUserName())) {
            cond.setUnitId(user.getUnitId());
        }
        if (!StringUtils.isEmpty(cond.getInvoiceTimeFrom())) {
            cond.setInvoiceTimeFrom(cond.getInvoiceTimeFrom() + " 00:00:00");
        }
        if (!StringUtils.isEmpty(cond.getInvoiceTimeTo())) {
            cond.setInvoiceTimeTo(cond.getInvoiceTimeTo() + " 23:59:59");
        }
    }

    private RelocationInvoice translation(InvoiceResVO invoiceVo) {
        RelocationInvoice invoice = new RelocationInvoice();
        // 转换单位
        BeanUtils.copyProperties(invoiceVo, invoice);
        List<Unit> list = unitApiExp.getAllUnitList();
        Map<String, Integer> unitMap = list.stream().collect(Collectors.toMap(Unit::getUnitName, Unit::getId));
        String remake = invoiceVo.getRemake();
        // 按照英文分隔符划分
        List<String> arrList = Arrays.asList(remake.split(";"));
        // 按照中文分隔符划分
        List<String> brrList = Arrays.asList(remake.split("；"));
        if (arrList.size() != 4 && brrList.size() != 4) {
            throw new InvoiceException(InvoiceErrorCode.RELOCATION_INVOICE_REMAKE_ERROR);

        }
        String[] split = remake.split("；");
        //合同号
        String contractNum = split[0];
        //区县
        String unit = split[1];
        Integer unitId = unitMap.get(unit);
        //款项性质
        String amountType = split[2];
        Integer atype = null;
        switch (amountType) {
            case "尾款":
                atype = 2;
                break;
            case "预付款":
                atype = 1;
                break;
            case "决算款":
                atype = 3;
                break;
            default:
                break;
        }
        invoice.setPaymentType(atype);
        // 项目信息 匹配id
        String pinfo = split[3];
        Long pid = relocationInvoiceMapper.selectPidByCondition(contractNum, unitId, pinfo);
        if (pid == null) {
            throw new InvoiceException(InvoiceErrorCode.RELOCATION_INVOICE_REMAKE_ERROR);
        }
        invoice.setProjectId(pid);
        invoice.setInvoiceTime(DateUtil.string2DateYMD(invoiceVo.getInvoiceTime()));
        invoice.setState(Integer.valueOf(invoiceVo.getState()));
        invoice.setIsImport(Integer.valueOf(invoiceVo.getIsImport()));
        invoice.setDistrict(Integer.valueOf(invoiceVo.getDistrict()));
        return invoice;
    }

    private RelocationIncome getRelocationIncome(RelocationInvoice relocationInvoice, Long pid,
                                                 Integer atype) {
        RelocationIncome relocationIncome = new RelocationIncome();
        RelocationProject relocationProject = projectMapper.single(pid);
        relocationIncome.setCategory(1);
        relocationIncome.setUnitId(relocationInvoice.getUnitId());
        relocationIncome.setSupplier(relocationInvoice.getBuyerName());
        relocationIncome.setContractNum(relocationProject.getContractNum());
        relocationIncome.setContractName(relocationProject.getContractName());
        relocationIncome.setStartTime(relocationProject.getPlanStartTime());
        relocationIncome.setContractDeadline(relocationProject.getPlanEndTime());
        relocationIncome.setContractAmount(relocationProject.getCompensationAmount());
        relocationIncome.setInvoiceTime(relocationInvoice.getInvoiceTime());
        relocationIncome.setInvoiceNum(relocationInvoice.getInvoiceNumber());
        relocationIncome.setInvoiceType(relocationInvoice.getInvoiceType() == 1 ? InvoiceType.ELECTRONIC_PLAIN_INVOICE.value() : InvoiceType.ELECTRONIC_SPECIAL_INVOICE.value());
        relocationIncome.setAmount(relocationInvoice.getAmount());
        relocationIncome.setTax(relocationInvoice.getTaxAmount());
        relocationIncome.setTaxIncludeAmount(relocationInvoice.getTaxIncludeAmount());
        relocationIncome.setConstructionName(relocationProject.getProjectName());
        relocationIncome.setPaymentType(atype);
        relocationIncome.setIsReceived(1);
        relocationIncome.setAging(0);
        relocationIncome.setReceivable(relocationInvoice.getTaxIncludeAmount());
        relocationIncome.setReceived(new BigDecimal(0));
        relocationIncome.setUnreceived(relocationInvoice.getTaxIncludeAmount());
        // 匹配项目id 数据库暂无字段
        // relocationIncome.setProjectId(relocationInvoice.getProjectId());
        return relocationIncome;
    }

    @Override
    public void judgeFileName(String fileName) {
        int i = fileName.lastIndexOf(".");
        String name = fileName.substring(i);
        if (!(AllName.XLS.getValue().equals(name) || AllName.XLSX.getValue().equals(name))) {
            throw new RelocationException(RelocationErrorCode.FILE_DATA_NAME_ERROR);
        }
    }


}
