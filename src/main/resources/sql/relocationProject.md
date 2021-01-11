selectProjectByCond
===
```sql
          select
-- @pageTag(){
                rp.id                   as id,
                unit_id                 as unitId,
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
                cause                   as cause,
                file_id                as fileId
    -- @}
          from relocation_project rp
    -- @where(){
            -- @if(!isEmpty(cond.contractNum)){
                and contract_num like concat('%', #{cond.contractNum},'%')
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
     -- @}
     -- @pageIgnoreTag(){
             order by contract_num
    -- @}
```
selectProjectByCondList
===
```sql
          select
                rp.id                   as id,
                unit_id                 as unitId,
                project_name            as projectName,
                project_num             as projectNum,  
                plan_start_time         as planStartTime,
                plan_end_time           as planEndTime,
                actual_end_time         as actualEndTime,
                opposite_unit           as oppositeUnit,
                opposite_contacts       as oppositeContacts,
                opposite_contacts_num   as oppositeContactsNum,
                compensation_amount     as compensationAmount,
                compensation_sate       as compensationSate,
                contract_num            as contractNum,
                contract_name           as contractName
          from relocation_project rp
                where contract_num = #{cond.contractNum}         
                and unit_id = #{cond.unitId}                                	   
                and project_name = #{cond.projectName}
```

selectProjectById
===
```sql
         select
                rp.id                   as id,
                unit_id                 as unitId,
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
                cause                   as cause,
                file_id                as fileId
from relocation_project rp
where id = #{id}
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
    select
-- @pageTag(){
    t.*,
    t2.*,
    t3.*,
    IFNULL(t5.thisYearInvoiceAccount, 0) as thisYearInvoiceAccount,
    IFNULL(t5.thisYearReceivable, 0)     as thisYearReceivable,
    IFNULL(t5.thisYearDueIn, 0)          as thisYearDueIn,
    IFNULL(t5.thisYearCostProportion, 0) as thisYearCostProportion
-- @}
    from (
             select count(rp.id)                                                                  as compensationAmount,
                    sum(
                            IF(compensation_sate = 10, construction_budget + material_budget, 0)) as notContractAccount,
                    count(case when contract_num != '' then 0 end)                                as contractNumAmount,
                    (count(rp.id) - count(case when contract_num != '' then 0 end))               as notContractNumAmount,
                    (count(case when contract_num != '' then 0 end) / count(rp.id))               as compensationRatio,
                    count(distinct if(contract_num = '', null, contract_num))                     as contractAmount,
                    sum(compensation_amount)                                                      as contractAccount,
                    unit_id                                                                       as unitId
             from relocation_project rp
             where has_compensation = true
             group by unit_id
         ) t
             left join
    
         (select count(case when oneNotCostAmount not in (0) then 0 end
                     ) as oneNotCostAmount,
                 count(case when twoNotCostAmount not in (0) then 0 end
                     ) as twoNotCostAmount,
                 count(case when threeNotCostAmount not in (0) then 0 end
                     ) as threeNotCostAmount,
                 unit_id
          from (
                   select count(case
                                    when contract_duration <= 12 and compensation_sate not in (80, 50, 10, 0)
                                        then 0 end) as oneNotCostAmount,
                          count(case
                                    when contract_duration between 12 and 36 and
                                         compensation_sate not in (80, 50, 10, 0)
                                        then 0 end) as twoNotCostAmount,
                          count(case
                                    when contract_duration > 36 and compensation_sate not in (80, 50, 10, 0)
                                        then 0 end) as threeNotCostAmount,
    
                          unit_id
                   from relocation_project
                   group by unit_id, contract_num
               ) t1
          group by unit_id) t2 on t2.unit_id = t.unitId
             left join
         (select sum(construction_budget + material_budget)            as costTotal,
                 sum(anticipate_payment + final_payment)               as compensationTotal,
                 IFNull(sum(anticipate_payment + final_payment) /
                        sum(construction_budget + material_budget), 0) as costRation,
                 sum(IF(compensation_sate = 20 and contract_type != '框架类' and
                        compensation_amount != 0,
                        anticipate_payable, 0))                        as budgetNotAccount,
                 sum(IF(compensation_sate in (40, 60, 70),
                        compensation_amount - anticipate_payment,
                        0))                                                as finalNotPayment,
                 unit_id
          from relocation_project
          group by unit_id
         ) t3 on t3.unit_Id = t2.unit_id
    
             left join (select IFNULL(SUM(receivable), 0)                 as thisYearInvoiceAccount,
                               IFNULL(sum(received), 0)                   as thisYearReceivable,
                               IFNULL(sum(receivable - received), 0)      as thisYearDueIn,
                               IFNULL(sum(received) / sum(receivable), 0) as thisYearCostProportion,
                               ri.unit_id
                        from relocation_income ri
                        group by ri.unit_id
    ) t5 on t5.unit_id = t3.unit_id
            -- @where(){
                -- @if(!isEmpty(unitId)){
                    and unitId = #{unitId}
                -- @}
            -- @}
```  
selectCompensationAmount
===  
```sql
select id                       as id ,
       contract_num             as contractNum,
       material_budget          as materialBudget,
       compensation_amount      as compensationAmount,
       construction_budget      as constructionBudget,
       anticipate_payment       as anticipatePayment,
       final_payment            as finalPayment
from relocation_project
where contract_num in (#{join(list)})
 ```

selectSumConstructionBudget
===
 ```sql
    select  contract_num                as num ,
              sum(construction_budget)  as constructionBudget,
              sum(anticipate_payable)   as anticipatePayable,
              sum(anticipate_payment)   as anticipatePayment,
              sum(final_payment)        as finalPayment,
              sum(compensation_amount)  as compensationAmount
    from relocation_project
    where contract_num  is not null and contract_num !='' 
     -- @if(!isEmpty(list)){
        and  contract_num in (#{join(list)})
     -- @}
    group by contract_num
 ```
selectProject
===
```sql
        select id, contract_duration 
        from relocation_project
        where compensation_sate not in (10,80,0)
        and contract_duration is not null 
        and actual_end_time is not null 
```

selectProjectNumByProjectNum
===
```sql
        select project_num from relocation_project
        where compensation_sate = 80  and project_num in (#{join(list)})
```
selectProjectStatementListByUnitId
===
```sql
select 
    t.*,
    t2.*,
    t3.*,
    IFNULL(t5.thisYearInvoiceAccount, 0) as thisYearInvoiceAccount,
    IFNULL(t5.thisYearReceivable, 0)     as thisYearReceivable,
    IFNULL(t5.thisYearDueIn, 0)          as thisYearDueIn,
    IFNULL(t5.thisYearCostProportion, 0) as thisYearCostProportion
    from (
             select count(rp.id)                                                                  as compensationAmount,
                    sum(
                            IF(compensation_sate = 10, construction_budget + material_budget, 0)) as notContractAccount,
                    count(case when contract_num != '' then 0 end)                                as contractNumAmount,
                    (count(rp.id) - count(case when contract_num != '' then 0 end))               as notContractNumAmount,
                    (count(case when contract_num != '' then 0 end) / count(rp.id))               as compensationRatio,
                    count(distinct if(contract_num = '', null, contract_num))                     as contractAmount,
                    sum(compensation_amount)                                                      as contractAccount,
                    unit_id                                                                       as unitId
             from relocation_project rp
             where has_compensation = true
             group by unit_id
         ) t
             left join
    
         (select count(case when oneNotCostAmount not in (0) then 0 end
                     ) as oneNotCostAmount,
                 count(case when twoNotCostAmount not in (0) then 0 end
                     ) as twoNotCostAmount,
                 count(case when threeNotCostAmount not in (0) then 0 end
                     ) as threeNotCostAmount,
                 unit_id
          from (
                   select count(case
                                    when contract_duration <= 12 and compensation_sate not in (80, 50, 10, 0)
                                        then 0 end) as oneNotCostAmount,
                          count(case
                                    when contract_duration between 12 and 36 and
                                         compensation_sate not in (80, 50, 10, 0)
                                        then 0 end) as twoNotCostAmount,
                          count(case
                                    when contract_duration > 36 and compensation_sate not in (80, 50, 10, 0)
                                        then 0 end) as threeNotCostAmount,
    
                          unit_id
                   from relocation_project
                   group by unit_id, contract_num
               ) t1
          group by unit_id) t2 on t2.unit_id = t.unitId
             left join
         (select sum(construction_budget + material_budget)            as costTotal,
                 sum(anticipate_payment + final_payment)               as compensationTotal,
                 IFNull(sum(anticipate_payment + final_payment) /
                        sum(construction_budget + material_budget), 0) as costRation,
                 sum(IF(compensation_sate = 20 and contract_type != '框架类' and
                        compensation_amount != 0,
                        anticipate_payable, 0))                        as budgetNotAccount,
                 sum(IF(compensation_sate in (40, 60, 70),
                        compensation_amount - anticipate_payment,
                        0))                                                as finalNotPayment,
                 unit_id
          from relocation_project
          group by unit_id
         ) t3 on t3.unit_Id = t2.unit_id
    
             left join (select IFNULL(SUM(receivable), 0)                 as thisYearInvoiceAccount,
                               IFNULL(sum(received), 0)                   as thisYearReceivable,
                               IFNULL(sum(receivable - received), 0)      as thisYearDueIn,
                               IFNULL(sum(received) / sum(receivable), 0) as thisYearCostProportion,
                               ri.unit_id
                        from relocation_income ri
                        group by ri.unit_id
    ) t5 on t5.unit_id = t3.unit_id
            -- @where(){
                -- @if(!isEmpty(unitId)){
                    and unitId = #{unitId}
                -- @}
            -- @}
  
``` 
selectProjectStartWarn
===
```sql
select rp.id                 as projectId,
       rp.project_num        as projectNum,
       rp.unit_id            as unitId,
       rp.construction_unit  as constructionUnit,
       rp.opposite_unit      as oppositeUnit,
       rp.contract_num       as contractNum,
       rp.anticipate_payment as anticipatePayment,
       rp.final_payment      as finalPayment,
       rp.contract_duration  as contractDuration,
       rp.compensation_sate       as compensationSate
from relocation_income ri
         left join relocation_project rp on ri.contract_num = rp.contract_num
where ri.received != 0 and rp.project_num is not null
```
selectProjectStartWarnCount
===
```sql
    select rp.unit_id   as unitId,
           count(rp.id) as count
    from relocation_income ri
             left join relocation_project rp on ri.contract_num = rp.contract_num
    where ri.received != 0
       and rp.contract_duration != 0 
      and rp.project_num is not null;
```
selectProjectFinalWarn
===
```sql
select id                 as projectId,
       project_num        as projectNum,
       rp.unit_id         as unitId,
       construction_unit  as constructionUnit,
       opposite_unit      as oppositeUnit,
       rp.contract_num    as contractNum,
       anticipate_payment as anticipatePayment,
       final_payment      as finalPayment,
       contract_duration  as contractDuration,
       compensation_sate  as compensationSate
from relocation_project rp
where contract_duration != 0
  and contract_duration is not null
  and compensation_sate != 10
  and compensation_sate != 80
  and contract_duration mod 2 = 0
``` 
selectProjectFinalWarnCount
===
```sql
    select unit_id as unitId,
           count(id)  as count
    from relocation_project rp
    where 
    contract_duration mod 2 = 0 
    and contract_duration is not null
    and compensation_sate != 10 
    and compensation_sate != 80
    and contract_duration != 0 
    group by unit_id;
```
selectNotCorrelationId
===
```sql
select id
from relocation_project
where id not in (select project_id from relocation_warn)
  and id not in (select project_id from relocation_invoice)
  and id not in (select project_id from relocation_receipt)
```
selectProjectIdByContractNum
===
```sql
select id
from relocation_project
where contract_num =#{contractNum}        
```
selectContractInfo
===
```sql
    select contract_num             as contractNum,
           contract_name            as contractName,
           min(plan_start_time)     as planStartTime,
           max(plan_end_time)       as planEndTime,
           sum(compensation_amount) as total
    from relocation_project
    where contract_num != ''
    group by contract_num
```
