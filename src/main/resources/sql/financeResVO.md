getFinanceList
===
```sql
select 
-- @pageTag(){
       rp.unit_id                                                          unitId,
       rp.project_type                                                     projectType,
       rp.eoms_repair_num                                                  eomsRepairNum,
       rp.project_name                                                     projectName,
       rp.plan_end_time                                                    planEndTime,
       rp.construction_budget + rp.material_budget                         estimatedCost,
       rp.construction_budget                                              constructionBudget,
       rp.construction_cost                                                constructionCost,
       rp.material_cost                                                    materialCost,
       rp.compensation_amount                                              compensationAmount,
       rp.plan_start_time                                                  planStartTime,
       rp.actual_end_time                                                  actualEndTime,
       rp.contract_num                                                     contractNum,
       rp.contract_name                                                    contractName,
       rp.opposite_unit                                                    oppositeUnit,
       rp.opposite_contacts                                                oppositeContacts,
       rp.compensation_amount                                              oppositeAmount,
       (rp.anticipate_payment + rp.final_payment)                          yearRecoveredAmount,
       rp.anticipate_payable                                               advanceReceivableAmount,
       rp.anticipate_payment                                               advanceReceivedAmount,
       if(rp.anticipate_payable = rp.anticipate_payment, 1, 0)             isAllReceived,
       (rp.compensation_amount - rp.anticipate_payment - rp.final_payment) unpaidCollection
-- @}
       from relocation_project rp
        -- @where(){
          -- @if(isNotEmpty(cond.unitId)){
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
            and rp.anticipate_payable-rp.anticipate_payment=0
          -- @}
          -- @if(cond.receiptStatus == 0){
            and rp.anticipate_payable-rp.anticipate_payment !=0
          -- @}
        -- @}
        -- @pageIgnoreTag(){
        order by rp.project_year desc
        -- @}
```
selectSumPayMonthAmount
===
```sql
select contract_num                                                                     as contractNum,
       ifnull(sum(case when pay_month = concat(#{year},  '01') then rid.amount end), 0) as janReceivable,
       ifnull(sum(case when pay_month = concat(#{year},  '02') then rid.amount end), 0) as febReceivable,
       ifnull(sum(case when pay_month = concat(#{year},  '03') then rid.amount end), 0) as marReceivable,
       ifnull(sum(case when pay_month = concat(#{year},  '04') then rid.amount end), 0) as aprReceivable,
       ifnull(sum(case when pay_month = concat(#{year},  '05') then rid.amount end), 0) as mayReceivable,
       ifnull(sum(case when pay_month = concat(#{year},  '06') then rid.amount end), 0) as juneReceivable,
       ifnull(sum(case when pay_month = concat(#{year},  '07') then rid.amount end), 0) as julReceivable,
       ifnull(sum(case when pay_month = concat(#{year},  '08') then rid.amount end), 0) as augReceivable,
       ifnull(sum(case when pay_month = concat(#{year},  '09') then rid.amount end), 0) as sepReceivable,
       ifnull(sum(case when pay_month = concat(#{year},  '10') then rid.amount end), 0) as octReceivable,
       ifnull(sum(case when pay_month = concat(#{year},  '11') then rid.amount end), 0) as novReceivable,
       ifnull(sum(case when pay_month = concat(#{year},  '12') then rid.amount end), 0) as decReceivable,
       IFNULL(sum(init_amount), 0)                                                      as initRecoveredAmount,
       IFNULL(sum(receivable), 0)                                                       as  invoicedAmount
from relocation_income_detail rid
         left join relocation_income ri on rid.income_id = ri.id
group by contract_num
```