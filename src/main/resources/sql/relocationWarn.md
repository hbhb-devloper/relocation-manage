selectProjectWarnByCond
===
   ```sql
        select project_num        as projectNum,
        u.unit_name        as unitName,
        construction_unit  as constructionUnit,
        opposite_unit      as oppositeUnit,
        rw.contract_num    as contractNum,
        anticipate_payment as anticipatePayment,
        rw.is_received        as isReceived,
        final_payment      as finalPayment,
        contract_duration  as contractDuration
        from relocation_warn rw
        left join unit u on rw.unit_id = u.id
        left join relocation_income ri on ri.contract_num = rw.contract_num
    -- @where(){
            state = 1
        -- @if(!isEmpty(cond.projectNum)){       
            and project_num like concat ('%',#{projectNum},'%')
        -- @}
        -- @if(!isEmpty(cond.contractNum)){    
           and rw.contract_num like concat ('%',#{contractNum},'%')
        -- @} 
        -- @if(!isEmpty(cond.contractDuration)){                
           and contract_duration = #{contractDuration}
        -- @} 
    ---@}
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
