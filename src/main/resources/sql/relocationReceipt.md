  
selectReceiptListByCond
===
 ```sql
select rr.id               as id,
       rr.category         as category,
       rr.unit_id          as unitId,
       receipt_num         as receiptNum,
       compensation_amount as compensationAmount,
       payment_amount      as paymentAmount,
       rr.contract_name    as contractName,
       rr.contract_num     as contractNum,
       payment_desc        as paymentDesc,
       receipt_amount      as receiptAmount,
       receipt_time        as receiptTime,
       remake              as remake,
       rr.supplier         as supplier,
       rii.is_received        paymentStatus,
       rii.receivable         receivable,
       rii.received           received,
       rii.unreceived         unreceived
from relocation_receipt rr
         left join relocation_income rii
                   on rr.receipt_num = rii.invoice_num
     -- @where(){
       -- @if(!isEmpty(cond.unitId)){
            and rr.unit_id = #{cond.unitId}
       -- @}
       -- @if(!isEmpty(cond.invoiceTimeFrom)){
        and rr.receipt_time >= #{cond.invoiceTimeFrom}
       -- @}
       -- @if(!isEmpty(cond.invoiceTimeTo)){
        and rr.receipt_time <= #{cond.invoiceTimeTo}
      -- @} 
      -- @if(!isEmpty(cond.amountFrom)){
        and rr.compensation_amount >= #{cond.amountFrom}
      -- @}
     -- @if(!isEmpty(cond.amountTo)){
        and rr.compensation_amount<= #{cond.amountTo}
      -- @} 
     -- @} 
```

selectReceiptByCond
===
 ```sql
      select 
 -- @pageTag(){
 rr.id               as id,
       rr.category         as category,
       rr.unit_id          as unitId,
       receipt_num         as receiptNum,
       compensation_amount as compensationAmount,
       payment_amount      as paymentAmount,
       rr.contract_name    as contractName,
       rr.contract_num     as contractNum,
       payment_desc        as paymentDesc,
       receipt_amount      as receiptAmount,
       receipt_time        as receiptTime,
       remake              as remake,
       rr.supplier         as supplier,
       rii.is_received     as  isReceived,
       rii.receivable      as  receivable,
       rii.received        as  received,
       rii.unreceived      as   unreceived
    -- @}
       from relocation_receipt rr
       left join relocation_income rii
       on rr.receipt_num = rii.invoice_num
     -- @where(){
       -- @if(!isEmpty(cond.unitId)){
            and rr.unit_id = #{cond.unitId}
       -- @}
       -- @if(!isEmpty(cond.invoiceTimeFrom)){
        and rr.receipt_time >= #{cond.invoiceTimeFrom}
       -- @}
       -- @if(!isEmpty(cond.invoiceTimeTo)){
        and rr.receipt_time <= #{cond.invoiceTimeTo}
      -- @} 
      -- @if(!isEmpty(cond.amountFrom)){
        and rr.compensation_amount >= #{cond.amountFrom}
      -- @}
     -- @if(!isEmpty(cond.amountTo)){
        and rr.compensation_amount<= #{cond.amountTo}
      -- @} 
     -- @} 
```

selectReceiptNum
===
```sql
select receipt_num from relocation_receipt
group by receipt_num
```

selectReceiptByReceiptNum
===
```sql
      select 
            rr.id               as id ,
             receipt_num         as receiptNum, 
             category            as category,
             unit_id             as unitId,
             compensation_amount as compensationAmount,
             payment_amount      as paymentAmount,
             contract_name       as contractName,
             contract_num        as contractNum,
             payment_desc        as paymentDesc,
             receipt_amount      as receiptAmount,
             receipt_time        as receiptTime,
             remake              as remake,
             supplier            as supplier
      from relocation_receipt rr
where receipt_num = #{receiptNum}
```