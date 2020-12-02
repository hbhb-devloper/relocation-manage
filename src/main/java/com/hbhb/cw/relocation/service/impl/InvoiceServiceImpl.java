package com.hbhb.cw.relocation.service.impl;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.*;
import com.hbhb.cw.relocation.exception.InvoiceException;
import com.hbhb.cw.relocation.exception.RelocationException;
import com.hbhb.cw.relocation.mapper.IncomeDetailMapper;
import com.hbhb.cw.relocation.mapper.IncomeMapper;
import com.hbhb.cw.relocation.mapper.InvoiceMapper;
import com.hbhb.cw.relocation.mapper.ProjectMapper;
import com.hbhb.cw.relocation.model.RelocationIncome;
import com.hbhb.cw.relocation.model.RelocationIncomeDetail;
import com.hbhb.cw.relocation.model.RelocationInvoice;
import com.hbhb.cw.relocation.rpc.SysUserApiExp;
import com.hbhb.cw.relocation.rpc.UnitApiExp;
import com.hbhb.cw.relocation.service.InvoiceService;
import com.hbhb.cw.relocation.web.vo.*;
import com.hbhb.cw.systemcenter.model.Unit;
import com.hbhb.cw.systemcenter.vo.UnitTopVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;
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
import java.util.List;
import java.util.Map;
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
    private IncomeDetailMapper detailMapper;

    @Override
    public PageResult<InvoiceResVO> getInvoiceList(Integer pageNum, Integer pageSize,
        InvoiceReqVO cond, Integer userId) {
        List<Unit> unitList = unitApiExp.getAllUnitList();
        UnitTopVO parentUnit = unitApiExp.getTopUnit();
        List<Integer> unitIds = new ArrayList<>();
        for (Unit unit : unitList) {
            if (parentUnit.getBenbu().equals(cond.getUnitId())) {
                unitIds.add(unit.getId());
            }
        }
        cond.setUnitIds(unitIds);
        PageRequest<InvoiceResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<InvoiceResVO> invoiceResVo = relocationInvoiceMapper
            .selectListByCondition(cond, request);
        Map<Integer, String> unitMap = unitList.stream()
            .collect(Collectors.toMap(Unit::getId, Unit::getUnitName));
        invoiceResVo.getList().forEach(item -> {
            item.setInvoiceTypeLabel(State.ONE.value().equals(item.getInvoiceType().toString())
                ? InvoiceType.PLAIN_INVOICE.value() : InvoiceType.SPECIAL_INVOICE.value());
            item.setState(State.ONE.value().equals(item.getState()) ? InvoiceSate.BLUE_STATE.value()
                : InvoiceSate.RED_STATE.value());
            item.setIsImport(
                State.ONE.value().equals(item.getState()) ? State.YES.value() : State.NO.value());
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
        RelocationInvoice single = relocationInvoiceMapper.single(invoiceVo.getInvoiceNumber());
        Integer re = relocationInvoiceMapper
            .selectListByNumber(invoiceVo.getInvoiceNumber());
        if (re > 0) {
            throw new InvoiceException(
                InvoiceErrorCode.RELOCATION_INVOICE_IMPORT_BUSTYPE_ERROR);
        }
        RelocationInvoice invoice = translation(invoiceVo);
        relocationInvoiceMapper.insert(invoice);
        //invoice.getProjectId(),invoice.getPaymentType()
        RelocationIncome relocationIncome = getRelocationIncome(invoice);
        relocationIncomeMapper.insert(relocationIncome);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSaveRelocationInvoice(List<InvoiceImportVO> dataList) {
        List<ProjectInfoVO> projectInfo = relocationInvoiceMapper.getProjectInfo();
        // todo map中可能出现重复的key导致报错
//        Map<String, Long> projectMap = projectInfo.stream()
//            .collect(Collectors.toMap(ProjectInfoVO::getInfo, ProjectInfoVO::getId));
        // TODO 发票匹配验证 本年度不做匹配
        // 查找发票备注格式
        //List<String> remakeList = relocationInvoiceMapper.selectInvoiceRemake();
        List<String> invoiceNumber = relocationInvoiceMapper.selectInvoiceNumber();
        // 转换单位
        List<Unit> list = unitApiExp.getAllUnitList();
        Map<String, Integer> unitMap = list.stream()
            .collect(Collectors.toMap(Unit::getUnitName, Unit::getId));
        List<RelocationInvoice> invoiceList = new ArrayList<>();
        List<RelocationIncome> incomeList = new ArrayList<>();
        List<RelocationIncomeDetail> incomeDetails = new ArrayList<>();
        long count = dataList.stream().distinct().count();
        //文件内查重
        if (count < dataList.size()) {
            throw new InvoiceException(
                InvoiceErrorCode.RELOCATION_INCOME_EXCEL_EXIST);
        }
        List<String> msg = new ArrayList<>();
        int i = 1;
        for (InvoiceImportVO relocationInvoiceImport : dataList) {
            boolean contains = invoiceNumber.contains(relocationInvoiceImport.getInvoiceNumber());
            if (contains) {
                msg.add("在excel表中第" + i + "行，数据编号为:" + relocationInvoiceImport.getNumber()
                    + "已存在发票表中\n");
            }
            RelocationInvoice relocationInvoice = new RelocationInvoice();
            RelocationIncome relocationIncome = new RelocationIncome();
            //地区默认11
            relocationInvoice.setDistrict(11);
            //经办单位
            relocationInvoice.setUnitId(unitMap.get(relocationInvoiceImport.getUnit()));
            //发票代码 空
            relocationInvoice.setInvoiceCode("");
            relocationInvoice.setInvoiceNumber(relocationInvoiceImport.getInvoiceNumber());
            String invoiceType = relocationInvoiceImport.getInvoiceType();
            // todo 发票类型 重新区别  需添加字典
            if ("电子增值税普通发票".equals(invoiceType) || "增值税电子普票".equals(invoiceType)
                || "增值税电子普通发票".equals(invoiceType) || "增值税普票".equals(invoiceType)) {
                relocationInvoice.setInvoiceType(1);
            }
            if ("电子增值税专用发票".equals(invoiceType) || "增值税专用发票".equals(invoiceType)) {
                relocationInvoice.setInvoiceType(0);
            }
            //购方税号 购方名称 开票项目
            /*relocationInvoice.setBuyerTax(relocationInvoiceImport.getBuyerTax());
            relocationInvoice.setBuyerName(relocationInvoiceImport.getBuyerName());
            relocationInvoice.setInvoiceProject(relocationInvoiceImport.getInvoiceProject());*/

            //开票日期格式转换 yyyy/MM/dd
            relocationInvoice
                .setInvoiceTime(DateUtil.string2DateYMD(relocationInvoiceImport.getInvoiceTime()));
            relocationInvoice.setAmount(new BigDecimal(relocationInvoiceImport.getAmount()));
            //税率 空
            relocationInvoice.setTaxRate(new BigDecimal("0"));
            //税额
            relocationInvoice
                .setTaxAmount(new BigDecimal(relocationInvoiceImport.getTaxAmount() == null ? "0"
                    : relocationInvoiceImport.getTaxAmount()));
            //价税合计
            relocationInvoice
                .setTaxIncludeAmount(new BigDecimal(relocationInvoiceImport.getTaxIncludeAmount()));
            //备注
            relocationInvoice.setRemake(relocationInvoiceImport.getRemake());
            //收款负责人/申请人
            relocationInvoice.setApplicant(relocationInvoiceImport.getPayee());
            //开票人 空
            //relocationInvoice.setIssuer("");
            //票据状态空 1 蓝字, 0 红字
            //relocationInvoice.setState("蓝字".equals(relocationInvoiceImport.getState()) ? 1 : 0);
            //是否自定义开票空 1 是, 0 否
            /*relocationInvoice
                .setIsImport("是".equals(relocationInvoiceImport.getIsImport()) ? 1 : 0);*/
            //款项类型空
            //relocationInvoice.setBusinessType(relocationInvoiceImport.getBusinessType());
            //客户经理空
            //relocationInvoice.setManager(relocationInvoiceImport.getManager());

            //==============添加 收款信息==================
            BeanUtils.copyProperties(relocationInvoiceImport, relocationIncome);
            //类别 todo 字典
            relocationIncome.setCategory("迁改".equals(relocationInvoiceImport.getCategory()) ? 1
                : "搬迁".equals(relocationInvoiceImport.getCategory()) ? 2 : 3);
            relocationIncome.setUnitId(unitMap.get(relocationInvoiceImport.getUnit()));
            //合同起始时间
            relocationIncome
                .setStartTime(DateUtil.string3DateYMD(relocationInvoiceImport.getStartTime()));
            //合同截止时间
            relocationIncome.setContractDeadline(
                DateUtil.string3DateYMD(relocationInvoiceImport.getContractDeadline()));
            //开票时间
            relocationIncome
                .setInvoiceTime(DateUtil.string3DateYMD(relocationInvoiceImport.getInvoiceTime()));
            // 10-已收款 20-未收款 30-部分回款
            relocationIncome.setIsReceived(
                IsReceived.NOT_RECEIVED.value().equals(relocationInvoiceImport.getCategory())
                    ? IsReceived.NOT_RECEIVED.key()
                    : IsReceived.PART_RECEIVED.value().equals(relocationInvoiceImport.getCategory())
                        ? IsReceived.PART_RECEIVED.key() : IsReceived.RECEIVED.key());
            //发票号码
            relocationIncome.setInvoiceNum(relocationInvoiceImport.getInvoiceNumber());
            //税额
            relocationIncome.setTax(new BigDecimal(relocationInvoiceImport.getTaxAmount()));
            //款项类型空
            // 应收已收未收 复制成功?

            // todo 导入发票成功后放入收款
            invoiceList.add(relocationInvoice);
            //RelocationIncome relocationIncome = getRelocationIncome(relocationInvoice);
            //incomeList.add(relocationIncome);
            relocationIncomeMapper.insert(relocationIncome);
            // 收款编号有数据 插入收款详情
            if (StringUtils.isEmpty(relocationInvoiceImport.getReceiptNum())) {
                RelocationIncomeDetail incomeDetail = new RelocationIncomeDetail();
                incomeDetail.setPayMonth(String.valueOf(relocationInvoiceImport.getPaymentDay()));
                incomeDetail.setAmount(new BigDecimal(relocationInvoiceImport.getReceived()));
                incomeDetail.setCreateTime(DateUtil.getCurrentDate());
                //todo 收款id获取问题
                incomeDetail.setIncomeId(relocationIncome.getId());
                incomeDetail.setPayee(relocationInvoiceImport.getPayee());
                detailMapper.insert(incomeDetail);
            }
        }
        if (!msg.isEmpty()) {
            throw new InvoiceException(InvoiceErrorCode.RELOCATION_INCOME_NUMBER_EXIST,
                msg.toString());
        }
        relocationInvoiceMapper.insertBatch(invoiceList);
        //relocationIncomeMapper.insertBatch(incomeList);
    }

    @Override
    public List<InvoiceExportResVO> selectExportListByCondition(InvoiceReqVO vo, Integer userId) {
        setConditionDetail(vo, userId);
        List<Unit> unitList = unitApiExp.getAllUnitList();
        List<InvoiceExportResVO> exportResVos = relocationInvoiceMapper
            .selectExportListByCondition(vo);
        Map<Integer, String> unitMap = unitList.stream()
            .collect(Collectors.toMap(Unit::getId, Unit::getUnitName));
        exportResVos.forEach(item -> {
            item.setInvoiceType(
                State.ONE.value().equals(item.getInvoiceType()) ? InvoiceType.PLAIN_INVOICE.value()
                    : InvoiceType.SPECIAL_INVOICE.value());
            item.setState(State.ONE.value().equals(item.getState()) ? InvoiceSate.BLUE_STATE.value()
                : InvoiceSate.RED_STATE.value());
            item.setIsImport(
                State.ONE.value().equals(item.getState()) ? State.YES.value() : State.NO.value());
            item.setUnit(unitMap.get(item.getUnitId()));
            item.setDistrict(unitMap.get(item.getDistrictId()));
        });
        return exportResVos;
    }


    private void setConditionDetail(InvoiceReqVO cond, Integer userId) {
        UserInfo user = sysUserApiExp.getUserById(userId);
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
        Map<String, Integer> unitMap = list.stream()
            .collect(Collectors.toMap(Unit::getUnitName, Unit::getId));
        // todo 备注列不再有用
        /*String remake = invoiceVo.getRemake();
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
        }*/
        //invoice.setProjectId(pid);
        invoice.setInvoiceTime(DateUtil.string2DateYMD(invoiceVo.getInvoiceTime()));
        invoice.setState(Integer.valueOf(invoiceVo.getState()));
        invoice.setIsImport(Integer.valueOf(invoiceVo.getIsImport()));
        invoice.setDistrict(Integer.valueOf(invoiceVo.getDistrict()));
        return invoice;
    }

    private RelocationIncome getRelocationIncome(RelocationInvoice relocationInvoice) {
        RelocationIncome relocationIncome = new RelocationIncome();
        //RelocationProject relocationProject = projectMapper.single(pid);
        relocationIncome.setCategory(1);
        relocationIncome.setUnitId(relocationInvoice.getUnitId());
        relocationIncome.setSupplier(relocationInvoice.getBuyerName());
        // todo 发票信息获取
        //relocationIncome.setContractNum(relocationProject.getContractNum());
        //relocationIncome.setContractName(relocationProject.getContractName());
        //relocationIncome.setStartTime(relocationProject.getPlanStartTime());
        //relocationIncome.setContractDeadline(relocationProject.getPlanEndTime());
        //relocationIncome.setContractAmount(relocationProject.getCompensationAmount());
        relocationIncome.setInvoiceTime(relocationInvoice.getInvoiceTime());
        relocationIncome.setInvoiceNum(relocationInvoice.getInvoiceNumber());
        relocationIncome.setInvoiceType(
            relocationInvoice.getInvoiceType() == 1 ? InvoiceType.ELECTRONIC_PLAIN_INVOICE.value()
                : InvoiceType.ELECTRONIC_SPECIAL_INVOICE.value());
        relocationIncome.setAmount(relocationInvoice.getAmount());
        relocationIncome.setTax(relocationInvoice.getTaxAmount());
        relocationIncome.setTaxIncludeAmount(relocationInvoice.getTaxIncludeAmount());
        //relocationIncome.setConstructionName(relocationProject.getProjectName());
        //relocationIncome.setPaymentType(atype);
        relocationIncome.setIsReceived(1);
        // todo 账龄从合同日期到现在?
        relocationIncome.setAging(0);
        relocationIncome.setReceivable(relocationInvoice.getTaxIncludeAmount());
        relocationIncome.setReceived(new BigDecimal(0));
        relocationIncome.setUnreceived(relocationInvoice.getTaxIncludeAmount());
        // todo 匹配项目id 数据库暂无字段
        // relocationIncome.setProjectId(relocationInvoice.getProjectId());
        return relocationIncome;
    }

    @Override
    public void judgeFileName(String fileName) {
        int i = fileName.lastIndexOf(".");
        String name = fileName.substring(i);
        if (!(ExcelTypeEnum.XLS.getValue().equals(name) || ExcelTypeEnum.XLSX.getValue()
            .equals(name))) {
            throw new RelocationException(RelocationErrorCode.FILE_DATA_NAME_ERROR);
        }
    }


}
