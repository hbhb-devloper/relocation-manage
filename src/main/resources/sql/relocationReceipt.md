  
  selectReceiptByCond
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
     cause                   as cause
     -- @}
         from relocation_project rp
        -- @where(){
        -- @if(!isEmpty(cond.contractNum)){
            and contract_num like concat('%', #{cond.contractNum},'%')
        -- @}
        -- @if(!isEmpty(cond.unitId)){
            and unit_id = #{unitId}
        -- @}
        -- @if(!isEmpty(cond.projectNum)){
            and project_num like concat('%', #{cond.projectNum},'%')
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
```

  selectReceiptByCond
  ===
 ```sql
   select
        rp.id as id ,
        unit_id as unitId,
        project_name as projectName ,
        project_num as projectNum,
        project_type as projectType ,
        project_year as projectYear,
        project_month as projectMonth,
        eoms_repair_num as eomsRepairNum,
        eoms_cut_num as eomsCutNum,
        plan_start_time as planStartTime,
        plan_end_time as planEndTime,
        actual_end_time as actualEndTime,
        network_hierarchy as networkHierarchy,
        construction_budget as constructionBudget,
        construction_cost as constructionCost,
        construction_audit_cost as constructionAuditCost,
        construction_unit as constructionUnit,
        material_budget as materialBudget,
        material_cost as materialCost,
        opposite_unit as oppositeUnit,
        opposite_contacts as oppositeContacts,
        opposite_contacts_num as oppositeContactsNum,
        has_compensation as hasCompensation,
        compensation_type as compensationType,
        compensation_amount as compensationAmount,
        compensation_sate as compensationSate,
        compensation_remake as compensationRemake,
        contract_num as contractNum,
        contract_type as contractType,
        contract_name as contractName,
        contract_duration as contractDuration,
        is_initiative as isInitiative,
        anticipate_payable as anticipatePayable,
        anticipate_payment as anticipatePayment,
        final_payment as finalPayment,
        cause as cause
        from relocation_project rp
        -- @where(){
        -- @if(!isEmpty(cond.contractNum)){
            and contract_num like concat('%', #{cond.contractNum},'%')
        -- @}
        -- @if(!isEmpty(cond.unitId)){
            and unit_id = #{unitId}
        -- @}
        -- @if(!isEmpty(cond.projectNum)){
            and project_num like concat('%', #{cond.projectNum},'%')
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
```