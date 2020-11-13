getFinanceList
===
```sql
    select 
        -- @pageTag(){
            rp.unit_id unitId,
            rp.project_type projectType,
            rp.eoms_repair_num eomsRepairNum,
            rp.project_name projectName,
            rp.plan_end_time planEndTime,
            rp.construction_budget + rp.material_budget estimatedCost,
            rp.construction_cost constructionCost,
            rp.material_cost materialCost,
            rp.compensation_amount compensationAmount,
            rp.plan_start_time planStartTime,
            rp.actual_end_time actualEndTime,
            rp.contract_num contractNum,
            rp.contract_name contractName,
            rp.opposite_unit oppositeUnit,
            rp.opposite_contacts oppositeContacts,
            rp.compensation_amount oppositeAmount,
            ri.init_amount initRecoveredAmount,
            (rp.anticipate_payment + rp.final_payment) yearRecoveredAmount,
            rp.anticipate_payable advanceReceivableAmount,
            rp.anticipate_payment advanceReceivedAmount,
            if(rp.anticipate_payable = rp.anticipate_payment, 1, 0) isAllReceived,
            ifnull((select sum(amount) amount
            from relocation_income_detail
            where income_id = ri.id
            and pay_month = concat(#{cond.year}, '01')),0) janReceivable,
            ifnull((select sum(amount) amount
            from relocation_income_detail
            where income_id = ri.id
            and pay_month = concat(#{cond.year}, '02')),0) febReceivable,
            ifnull((select sum(amount) amount
            from relocation_income_detail
            where income_id = ri.id
            and pay_month = concat(#{cond.year}, '03')),0) marReceivable,
            ifnull((select sum(amount) amount
            from relocation_income_detail
            where income_id = ri.id
            and pay_month = concat(#{cond.year}, '04')),0) aprReceivable,
            ifnull((select sum(amount) amount
            from relocation_income_detail
            where income_id = ri.id
            and pay_month = concat(#{cond.year}, '05')),0) mayReceivable,
            ifnull((select sum(amount) amount
            from relocation_income_detail
            where income_id = ri.id
            and pay_month = concat(#{cond.year}, '06')),0) juneReceivable,
            ifnull((select sum(amount) amount
            from relocation_income_detail
            where income_id = ri.id
            and pay_month = concat(#{cond.year}, '07')),0) julReceivable,
            ifnull((select sum(amount) amount
            from relocation_income_detail
            where income_id = ri.id
            and pay_month = concat(#{cond.year}, '08')),0) augReceivable,
            ifnull((select sum(amount) amount
            from relocation_income_detail
            where income_id = ri.id
            and pay_month = concat(#{cond.year}, '09')),0) sepReceivable,
            ifnull((select sum(amount) amount
            from relocation_income_detail
            where income_id = ri.id
            and pay_month = concat(#{cond.year}, '10')),0) octReceivable,
            ifnull((select sum(amount) amount
            from relocation_income_detail
            where income_id = ri.id
            and pay_month = concat(#{cond.year}, '11')),0) novReceivable,
            ifnull((select sum(amount) amount
            from relocation_income_detail
            where income_id = ri.id
            and pay_month = concat(#{cond.year}, '12')),0) decReceivable,
            (rp.compensation_amount - rp.anticipate_payment - rp.final_payment) unpaidCollection,
            rii.amount invoicedAmount
            -- @}
            from relocation_project rp
            left join relocation_invoice rii on rp.id = rii.project_id
            left join relocation_income ri on rp.contract_num like ri.contract_num
        -- @where(){
          -- @if(cond.unitId == 429){
            and rp.unit_id in (SELECT id FROM unit WHERE parent_id = 429)
          -- @}
          -- @if(isNotEmpty(cond.unitId) && cond.unitId != 11 && cond.unitId != 429){
            and rp.unit_id = #{cond.unitId}
          -- @}
          -- @if(isNotEmpty(cond.projectType)){
            and rp.project_type = #{cond.projectType}
          -- @}
          -- @if(isNotEmpty(cond.projectName)){
            and rp.project_name like concat('%', #{cond.projectName},'%')
          -- @}
          -- @if(isNotEmpty(cond.planEndTime)){
            and rp.plan_end_time like concat('%', #{cond.planEndTime},'%')
          -- @}
          -- @if(isNotEmpty(cond.projectTime)){
            and rp.plan_start_time like concat('%', #{cond.projectTime},'%')
          -- @}
          -- @if(isNotEmpty(cond.contractNum)){
            and rp.contract_num like concat('%', #{cond.contractNum},'%')
          -- @}
          -- @if(cond.receiptStatus == 1){
            and rp.anticipate_payable = rp.anticipate_payment
          -- @}
        -- @}
        -- @pageIgnoreTag(){
        order by rp.project_year desc
        -- @}
```