  
selectReceiptListByCond
===
 ```sql
      select 
            rr.id               as id ,
             category            as category,
             unit_id             as unitId,
             compensation_amount as compensationAmount,
             payment_amount      as paymentAmount,
             contract_name       as contractName,
             contract_num        as contractNum,
             payment_desc        as paymentDesc,
             receipt_amount      as receiptAmount,
             receipt_time        as receiptTime,
             remake              as remake
      from relocation_receipt rr
     -- @where(){
       -- @if(!isEmpty(cond.unitId)){
            and rr.unit_id = #{cond.unitId}
       -- @}
       -- @if(!isEmpty(cond.invoiceTimeFrom)){
        and rr.receipt_time between #{cond.invoiceTimeFrom}
        and #{cond.invoiceTimeTo}
       -- @} 
      -- @if(!isEmpty(cond.amountFrom)){
        and rr.compensation_amount between #{cond.amountFrom}
        and #{cond.amountTo}
      -- @} 
     -- @} 
```

selectReceiptByCond
===
 ```sql
      select 
 -- @pageTag(){
            rr.id               as id ,
             category            as category,
             unit_id             as unitId,
             compensation_amount as compensationAmount,
             payment_amount      as paymentAmount,
             contract_name       as contractName,
             contract_num        as contractNum,
             payment_desc        as paymentDesc,
             receipt_amount      as receiptAmount,
             receipt_time        as receiptTime,
             remake              as remake
    -- @}
      from relocation_receipt rr
     -- @where(){
       -- @if(!isEmpty(cond.unitId)){
            and rr.unit_id = #{cond.unitId}
       -- @}
       -- @if(!isEmpty(cond.invoiceTimeFrom)){
        and rr.receipt_time between #{cond.invoiceTimeFrom}
        and #{cond.invoiceTimeTo}
      -- @} 
      -- @if(!isEmpty(cond.amountFrom)){
        and rr.compensation_amount between #{cond.amountFrom}
        and #{cond.amountTo}
      -- @} 
     -- @} 
```