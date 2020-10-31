package com.hbhb.cw.relocation.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.InvoiceErrorCode;
import com.hbhb.cw.relocation.exception.InvoiceException;
import com.hbhb.cw.relocation.mapper.IncomeMapper;
import com.hbhb.cw.relocation.mapper.InvoiceMapper;
import com.hbhb.cw.relocation.mapper.RelocationProjectMapper;
import com.hbhb.cw.relocation.model.RelocationIncome;
import com.hbhb.cw.relocation.model.RelocationInvoice;
import com.hbhb.cw.relocation.model.RelocationProject;
import com.hbhb.cw.relocation.rpc.SysUserApiExp;
import com.hbhb.cw.relocation.rpc.UnitApiExp;
import com.hbhb.cw.relocation.service.InvoiceService;
import com.hbhb.cw.relocation.web.vo.InvoiceExportResVO;
import com.hbhb.cw.relocation.web.vo.InvoiceImportVO;
import com.hbhb.cw.relocation.web.vo.InvoiceReqVO;
import com.hbhb.cw.relocation.web.vo.InvoiceResVO;
import com.hbhb.cw.relocation.web.vo.ProjectInfoVO;
import com.hbhb.cw.systemcenter.model.Unit;
import com.hbhb.cw.systemcenter.vo.SysUserInfo;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    private RelocationProjectMapper relocationProjectMapper;

    @Override
    public PageResult<InvoiceResVO> getInvoiceList(Integer pageNum, Integer pageSize,
        InvoiceReqVO cond, Integer userId) {
        PageRequest<InvoiceResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<InvoiceResVO> invoiceResVOPageResult = relocationInvoiceMapper
            .selectListByCondition(cond, request);
        List<InvoiceResVO> list = invoiceResVOPageResult.getList();
        for (InvoiceResVO invoiceResVO : list) {
            if ("1".equals(invoiceResVO.getInvoiceType())) {
                invoiceResVO.setInvoiceType("增值税普通发票");
            }
            if ("0".equals(invoiceResVO.getInvoiceType())) {
                invoiceResVO.setInvoiceType("增值税专用发票");
            }
            if ("1".equals(invoiceResVO.getState())) {
                invoiceResVO.setState("蓝字");
            }
            if ("0".equals(invoiceResVO.getState())) {
                invoiceResVO.setState("红字");
            }
            if ("1".equals(invoiceResVO.getIsImport())) {
                invoiceResVO.setIsImport("是");
            }
            if ("0".equals(invoiceResVO.getIsImport())) {
                invoiceResVO.setIsImport("否");
            }
        }
        return invoiceResVOPageResult;
    }

    @Override
    public RelocationInvoice getInvoiceDetail(Long id) {
        return relocationInvoiceMapper.single(id);
    }

    @Override

    public void updateInvoice(RelocationInvoice invoice) {
        translation(invoice);
        relocationInvoiceMapper.updateById(invoice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addInvoice(RelocationInvoice invoice) {
        translation(invoice);
        relocationInvoiceMapper.insert(invoice);
        RelocationIncome relocationIncome = getRelocationIncome(invoice,
            invoice.getProjectId(), invoice.getPaymentType());
        relocationIncomeMapper.insert(relocationIncome);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSaveRelocaxtionInvoice(List<InvoiceImportVO> dataList) {

        List<ProjectInfoVO> projectInfo = relocationInvoiceMapper.getProjectInfo();
        Map<String, Long> projectMap = projectInfo.stream()
            .collect(Collectors.toMap(ProjectInfoVO::getInfo, ProjectInfoVO::getId));
        // 转换单位
        List<Unit> list = unitApiExp.getAllUnitList();
        Map<String, Integer> unitMap = list.stream().collect(
            Collectors.toMap(Unit::getUnitName, Unit::getId));
        List<RelocationInvoice> invoiceList = new ArrayList<>();
        for (InvoiceImportVO relocationInvoiceImport : dataList) {
            if (!"财务开票".equals(relocationInvoiceImport.getBusinessType())) {
                throw new InvoiceException(
                    InvoiceErrorCode.RELOCATION_INVOICE_IMPORT_BUSTYPE_ERROR);
            }
            String newRemake = relocationInvoiceImport.getNewRemake();
            Matcher m = Pattern.compile(Pattern.quote("；")).matcher(newRemake);
            int i = 0;
            while (m.find()) {
                i++;
            }
            if (i != 3) {
                throw new InvoiceException(InvoiceErrorCode.RELOCATION_INVOICE_REMAKE_ERROR);
            }
            RelocationInvoice relocationInvoice = new RelocationInvoice();
            relocationInvoice.setUnitId(109);
            //relocationInvoice.setUnitId(unitMap.get(relocationInvoiceImport.getUnitId()));
            relocationInvoice.setDistrict(109);
            //relocationInvoice.setDistrict(unitMap.get(relocationInvoiceImport.getUnitId()));
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
            relocationInvoice
                .setInvoiceTime(relocationInvoiceImport.getInvoiceTime().split("\\.")[0]);
            relocationInvoice.setAmount(new BigDecimal(relocationInvoiceImport.getAmount()));
            relocationInvoice.setTaxRate(new BigDecimal(
                relocationInvoiceImport.getTaxRate() == null ? "0"
                    : relocationInvoiceImport.getTaxRate()));
            relocationInvoice.setTaxAmount(new BigDecimal(
                relocationInvoiceImport.getTaxAmount() == null ? "0"
                    : relocationInvoiceImport.getTaxAmount()));
            relocationInvoice
                .setTaxIncludeAmount(new BigDecimal(relocationInvoiceImport.getTaxIncludeAmount()));
            relocationInvoice.setRemake(relocationInvoiceImport.getRemake());
            relocationInvoice.setApplicant(relocationInvoiceImport.getApplicant());
            relocationInvoice.setIssuer(relocationInvoiceImport.getIssuer());
            // 1 蓝字, 0 红字
            relocationInvoice.setState("蓝字".equals(relocationInvoiceImport.getState()) ? 1 : 0);
            // 1 是, 0 否
            relocationInvoice
                .setIsImport("是".equals(relocationInvoiceImport.getIsImport()) ? 1 : 0);
            relocationInvoice.setBusinessType(relocationInvoiceImport.getBusinessType());
            relocationInvoice.setInvoiceSite(relocationInvoiceImport.getInvoiceSite());
            relocationInvoice.setManager(relocationInvoiceImport.getManager());
            Long pid = null;
            Integer atype = null;
            if (!StringUtils.isEmpty(newRemake) && !"作废".contains(newRemake) && !"发票作废"
                .equals(newRemake)) {
                String[] split = newRemake.split("；");
                //合同号
                String contractNum = split[0];
                if (contractNum.startsWith("（")) {
                    contractNum = contractNum
                        .substring(contractNum.indexOf("（") + 1, contractNum.lastIndexOf("）"));
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
                }
                //项目信息 匹配id
                String pinfo = split[3];
                String key = contractNum + unitId + pinfo;
                pid = projectMap.get(key);
                if (pid == null) {
                    throw new InvoiceException(
                        InvoiceErrorCode.RELOCATION_INVOICE_EXIST_PROJECT_ERROR);
                }
            }
            relocationInvoice.setPaymentType(atype);
            relocationInvoice.setProjectId(pid);
            invoiceList.add(relocationInvoice);
        }
        relocationInvoiceMapper.insertBatch(invoiceList);
    }

    @Override
    public List<InvoiceExportResVO> selectExportListByCondition(InvoiceReqVO vo,
        Integer userId) {
        setConditionDetail(vo, userId);
        List<InvoiceExportResVO> exportResVOS = relocationInvoiceMapper
            .selectExportListByCondition(vo);
        for (int i = 0; i < exportResVOS.size(); i++) {
            exportResVOS.get(i).setNum(i + 1);
            InvoiceExportResVO invoiceExportResVO = exportResVOS.get(i);
            if ("1".equals(invoiceExportResVO.getInvoiceType())) {
                invoiceExportResVO.setInvoiceType("增值税普通发票");
            }
            if ("0".equals(invoiceExportResVO.getInvoiceType())) {
                invoiceExportResVO.setInvoiceType("增值税专用发票");
            }
            if ("1".equals(invoiceExportResVO.getState())) {
                invoiceExportResVO.setState("蓝字");
            }
            if ("0".equals(invoiceExportResVO.getState())) {
                invoiceExportResVO.setState("红字");
            }
            if ("1".equals(invoiceExportResVO.getIsImport())) {
                invoiceExportResVO.setIsImport("是");
            }
            if ("0".equals(invoiceExportResVO.getIsImport())) {
                invoiceExportResVO.setIsImport("否");
            }
        }
        return exportResVOS;
    }


    @Override
    public void judgeFileName(String fileName) {
        int i = fileName.lastIndexOf(".");
        String name = fileName.substring(i);
        if (!(".xlsx".equals(name) || ".xls".equals(name))) {
            throw new InvoiceException(InvoiceErrorCode.FILE_NAME_ERROR);
        }
    }

    /**
     * 判断登录用户 添加时间查询 时分秒详情
     * @param cond
     * @param userId
     */
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

    private void translation(RelocationInvoice invoice) {
        // 转换单位
        List<Unit> list = unitApiExp.getAllUnitList();
        Map<String, Integer> unitMap = list.stream().collect(
            Collectors.toMap(Unit::getUnitName, Unit::getId));
        String remake = invoice.getRemake();
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
        }
        invoice.setPaymentType(atype);
        //项目信息 匹配id
        String pinfo = split[3];
        Long pid = relocationInvoiceMapper
            .selectPidByCondition(contractNum, unitId, pinfo);
        invoice.setProjectId(pid);
    }

    private RelocationIncome getRelocationIncome(RelocationInvoice relocationInvoice, Long pid,
        Integer atype) {
        RelocationIncome relocationIncome = new RelocationIncome();
        RelocationProject relocationProject = relocationProjectMapper.single(pid);
        relocationIncome.setCategory(1);
        relocationIncome.setUnitId(relocationInvoice.getUnitId());
        relocationIncome.setSupplier(relocationInvoice.getBuyerName());
        relocationIncome.setContractNum(relocationProject.getContractNum());
        relocationIncome.setContractName(relocationProject.getContractName());
        relocationIncome.setStartTime(relocationProject.getPlanStartTime());
        relocationIncome.setContractDeadline(relocationProject.getPlanEndTime());
        relocationIncome.setContractAmount(relocationProject.getCompensationAmount());
        relocationIncome
            .setInvoiceTime(DateUtil.stringToDate(relocationInvoice.getInvoiceTime()));
        relocationIncome.setInvoiceNum(relocationInvoice.getInvoiceNumber());
        relocationIncome.setInvoiceType(
            relocationInvoice.getInvoiceType() == 1 ? "增值税电子普通发票" : "电子增值税专用发票");
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
        return relocationIncome;
    }
}
