
	
selectProjectByCond
===
```sql
          select
-- @pageTag(){
                rp.id                   as id,
                unit_id                 as unitId,
                u.unit_name             as unitName,
                project_name            as projectName,
                project_num             as projectNum,
                project_type            as projectType,
                project_year            as projectYear,
                project_month           as projectMonth,
                eoms_repair_num         as eomsRepairNum,
                eoms_cut_num            as eomsCutNum,
                plan_start_time         as planStartTime,
                plan_end_time           as planEndTime,
                actual_end_time         as actualEndTime,
                network_hierarchy       as networkHierarchy,
                construction_budget     as constructionBudget,
                construction_cost       as constructionCost,
                construction_audit_cost as constructionAuditCost,
                construction_unit       as constructionUnit,
                material_budget         as materialBudget,
                material_cost           as materialCost,
                opposite_unit           as oppositeUnit,
                opposite_contacts       as oppositeContacts,
                opposite_contacts_num   as oppositeContactsNum,
                has_compensation        as hasCompensation,
                compensation_type       as compensationType,
                compensation_amount     as compensationAmount,
                compensation_sate       as compensationSate,
                compensation_remake     as compensationRemake,
                contract_num            as contractNum,
                contract_type           as contractType,
                contract_name           as contractName,
                contract_duration       as contractDuration,
                is_initiative           as isInitiative,
                anticipate_payable      as anticipatePayable,
                anticipate_payment      as anticipatePayment,
                final_payment           as finalPayment,
                cause                   as cause
    -- @}
          from relocation_project rp
          left join unit u on rp.unit_id = u.id
    -- @where(){
            --  @if(!isEmpty(cond.contractNum)){
                 contract_num like concat('%', #{cond.contractNum},'%')
            -- @}
            -- @if(!isEmpty(cond.unitId)){
                and unit_id=#{cond.unitId}
            -- @}
            -- @if(!isEmpty(cond.projectNum)){
                and project_num  like concat('%', #{cond.projectNum},'%')
            -- @}
            -- @if(!isEmpty(cond.compensationSate)){
                and compensation_sate = #{cond.compensationSate}
            -- @}
            -- @if(!isEmpty(cond.contractDuration)){
                and contract_duration = #{cond.contractDuration}
            -- @}                                  	   
            -- @if(!isEmpty(cond.projectName)){
                and project_name like concat('%', #{cond.projectName},'%')
            -- @}
     -- 
```

selectProjectNum
===
```sql
    select project_num from relocation_project
```
selectContractNumList
===
```sql
    select distinct contract_num  from relocation_project
```

selectProjectStatementByUnitId
===
```sql
    SELECT
        -- @pageTag(){
        *
        -- @}
    FROM (
             select *
             from (
                      select count(rp.id)                                                                  as compensationAmount,
                             sum(
                                     IF(compensation_sate = 10, construction_budget + material_budget, 0)) as notContractAccount,
                             count(case when contract_num != '' then 0 end)                                as contractNumAmount,
                             (count(rp.id) - count(case when contract_num != '' then 0 end))               as notContractNumAmount,
                             (count(case when contract_num != '' then 0 end) / count(rp.id))               as compensationRatio,
                             count(distinct contract_num)                                                  as contractAmount,
                             sum(compensation_amount)                                                      as contractAccount,
                             unit_id                                                                       as unitId,
                             u.unit_name                                                                   as unitName
                      from relocation_project rp
                               left join unit u on u.id = rp.unit_id
                      where has_compensation = true
                      group by unit_id
                  ) t1
                      left join
                  (
                      select count(case when contract_duration >= 1 then 0 end)            as oneNotCostAmount,
                             count(case when contract_duration between 1 and 3 then 0 end) as twoNotCostAmount,
                             count(case when contract_duration > 3 then 0 end)             as threeNotCostAmount,
                             sum(construction_budget + material_budget)                    as costTotal,
                             sum(anticipate_payment + final_payment)                       as compensationTotal,
                             sum(construction_budget + material_budget) /
                             sum(anticipate_payment + final_payment)                       as costRation,
                             sum(IF(compensation_sate = 20 and contract_type != '框架类', compensation_amount,
                                    0))                                                    as budgetNotAccount,
                             (IF(compensation_sate not in (40, 50, 60, 70),
                                 sum(compensation_amount) - sum(anticipate_payment),
                                 0))                                                       as finalNotPayment,
                             unit_id
                      from relocation_project
                      group by unit_id
                  ) t2 on t1.unitId = t2.unit_id
         ) t3
             left join (select SUM(thisYearInvoiceAccount) as thisYearInvoiceAccount, unitId4 as unitId6
                        from (
                                 select *
                                 from (
                                          select sum(receipt_amount) as thisYearInvoiceAccount, unit_id as unitId4
                                          from relocation_receipt
                                          where year(receipt_time) = year(now())
                                          group by unit_id
                                      ) t4
                                 union
                                 (
                                     select sum(amount), unit_id as unitId5
                                     from relocation_invoice
                                     where year(invoice_time) = year(now())
                                     group by unit_id)
                             ) t5
                        group by unitId4) t6 on t3.unit_id = t6.unitId6
            -- @where(){
                -- @if(!isEmpty(unitId)){
                    and t3.unit_id=#{unitId}
                -- @}
            -- @}
  
```
selectProjectWarn
 ===
 ```sql
    select project_num        as projectNum,
           u.unit_name        as unitName,
           u.id               as unitId,
           construction_unit  as constructionUnit,
           opposite_unit      as oppositeUnit,
           rp.contract_num    as contractNum,
           anticipate_payment as anticipatePayment,
           ri.is_received     as isReceived,
           final_payment      as finalPayment,
           contract_duration  as contractDuration
    from relocation_project rp
             left join unit u on rp.unit_id = u.id
             left join relocation_income ri on ri.contract_num = rp.contract_num
    where contract_duration mod 3 = 0 and compensation_sate = !80
```   

selectCompensationAmount
  ===
```sql
select contract_num        as contractNum,
       compensation_amount as compensationAmount,
       construction_budget as constructionBudget
from relocation_project
where contract_num in (
  -- @for(item in list){
    #{item} #text(item)
  -- @}
    )
 ```

selectSumCompensationAmount
===
  ```sql
        select contract_num, sum(compensation_amount)
        from relocation_project
        group by contract_num
  ```

selectSumConstructionBudget
===
  ```sql
    select contract_num, sum(construction_budget)
    from relocation_project
    where contract_num in (
    -- @for(item in list){
        #{item} #text(item)
    -- @}
    )
    group by contract_num;
  ```
selectProject
===
  ```sql
        select id, contract_duration 
        from relocation_project
        where compensation_sate not in (10,80)
  ```

updateBatch
===
   ```sql
   -- @for(item in list){
            update relocation_project set
                contract_num = #{item.contractNum},
                compensation_amount = #{item. compensationAmount},
                construction_budget = #{item.constructionBudget}          
            where id = #{item.id}
    -- @}
 ```

selectProjectNumByProjectNum
===
   ```sql
    
        select project_num from relocation_project
        where compensation_sate = 80  and project_num in (
      -- @for(item in list){
         #{item}
      -- @}
      )
 ```
selectProjectWarn
===
 ```sql
    select project_num        as projectNum,
           u.id               as unitId,
           u.unit_name        as unitName,
           construction_unit  as constructionUnit,
           opposite_unit      as oppositeUnit,
           rp.contract_num    as contractNum,
           anticipate_payment as anticipatePayment,
           final_payment      as finalPayment,
           contract_duration  as contractDuration
    from relocation_project rp
             left join unit u on rp.unit_id = u.id
    where contract_duration mod 3 = 0 and compensation_sate != 80
```   