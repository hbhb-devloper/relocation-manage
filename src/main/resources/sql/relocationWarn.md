selectProjectWarnByCond
===
   ```sql
        select project_num        as projectNum,
        rw.unit_id        as unitId,
        construction_unit  as constructionUnit,
        opposite_unit      as oppositeUnit,
        rw.contract_num    as contractNum,
        anticipate_payment as anticipatePayment,
        rw.is_received        as isReceived,
        final_payment      as finalPayment,
        contract_duration  as contractDuration
        from relocation_warn rw
        left join relocation_income ri on ri.contract_num = rw.contract_num
    -- @where(){
            state = 1
        -- @if(!isEmpty(cond.projectNum)){       
            and project_num like concat ('%',#{cond.projectNum},'%')
        -- @}
        -- @if(!isEmpty(cond.contractNum)){    
           and rw.contract_num like concat ('%',#{cond.contractNum},'%')
        -- @} 
        -- @if(!isEmpty(cond.contractDuration)){                
           and contract_duration = #{cond.contractDuration}
        -- @} 
    -- @}
```

selectProjectNum
===
 ```sql
        select distinct project_num from relocation_warn
        where state = 1   
```

updateSateByProjectNum
===
  ```sql
     update relocation_warn
     set state = 0
 -- @for(item in list)             
     where project_num = #{item.label}
      -- @}
 ```

selectWarnCountByUnitId
===
  ```sql
    select count(id)
    from relocation_warn
    where unit_id = #{unitId}
      and id not in (
        select warn_id
        from relocation_file
    );
  ```