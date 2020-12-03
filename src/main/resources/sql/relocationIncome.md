getIncomeList
===
```sql
select 
    -- @pageTag(){
    ri.id,
    category,
    unit_id unit,
    supplier,
    contract_num contractNum,
    contract_name contractName,
    start_time startTime,
    contract_deadline contractDeadline,
    contract_amount contractAmount,
    invoice_time invoiceTime,
    invoice_num invoiceNum,
    invoice_type invoiceType,
    invoice_type invoiceTypeLabel,
    amount,
    tax,
    tax_include_amount taxIncludeAmount,
    construction_name constructionName,
    payment_type paymentType,
    is_received isReceived,
    aging,
    receivable,
    received,
    unreceived
    -- @}
    from relocation_income ri
  
    -- @where(){
      -- @if(cond.unitId == 429){
        and ri.unit_id in (#{cond.unitIds})
      -- @}
      -- @if(isNotEmpty(cond.unitId) && cond.unitId != 11 && cond.unitId != 429){
        and ri.unit_id = #{cond.unitId}
      -- @}
      -- @if(isNotEmpty(cond.contractDeadlineFrom)){
        and contract_deadline >= #{cond.contractDeadlineFrom}
      -- @}
      -- @if(isNotEmpty(cond.contractDeadlineTo)){
        and contract_deadline <= #{cond.contractDeadlineTo}
      -- @}
      -- @if(isNotEmpty(cond.contractName)){
        and contract_name like concat('%', #{cond.contractName},'%')
      -- @}
      -- @if(isNotEmpty(cond.contractNum)){
        and contract_num like concat('%', #{cond.contractNum},'%')
      -- @}
      -- @if(isNotEmpty(cond.startTimeFrom)){
        and start_time >= #{cond.startTimeFrom}
      -- @}
      -- @if(isNotEmpty(cond.startTimeTo)){
        and start_time <= #{cond.startTimeTo}
      -- @}
    -- @}
    -- @pageIgnoreTag(){
    order by ri.invoice_time desc
    -- @}
```

selectDetailById
===
```sql
    select amount,
        receipt_num receiptNum,
        pay_month payMonth,
        payee
    from relocation_income_detail
    where income_id = #{id} 
    -- @if(isNeed == 1){
    and pay_month = #{currentMonth}
    -- @}
    order by pay_month desc
```

addIncomeDetail
===
```sql
    insert into relocation_income_detail (id, income_id, amount,
      receipt_num, pay_month, payee,
      create_time)
    values (#{detail.id}, #{detail.incomeId}, #{detail.amount},
      #{detail.receiptNum}, #{detail.payMonth}, #{detail.payee},
      current_timestamp())
```
updateIncomeDetail
===
```sql
update relocation_income_detail set
   -- @trim(){
      -- @if(isNotEmpty(detail.amount)){
        amount = #{detail.amount},
      -- @}
      -- @if(isNotEmpty(detail.receiptNum)){
        receipt_num = #{detail.receiptNum},
      -- @}
      -- @if(isNotEmpty(detail.payMont)){
        pay_month = #{detail.payMont},
      -- @}
      -- @if(isNotEmpty(detail.payee)){
        payee = #{detail.payee},
      -- @}
    -- @}
    where id = #{detail.id}
```

updateIsReceived
===
```sql
    UPDATE relocation_income
    SET is_received = #{i}
    where id = #{id}
```

updateIncomeUnreceived
===
```sql
    UPDATE relocation_income
    SET unreceived = (select unreceived from 
    relocation_income where id = #{id}) - #{amount}
    where id = #{id}
```

updateIncomeReceived
===
```sql
    UPDATE relocation_income
    SET received = (select received from relocation_income 
    where id = #{id}) + #{amount}
    where id = #{id}
```

selectExportList
===
```sql
    select
    category,
    u.short_name unit,
    supplier,
    contract_num contractNum,
    contract_name contractName,
    start_time startTime,
    contract_deadline contractDeadline,
    contract_amount contractAmount,
    invoice_time invoiceTime,
    invoice_num invoiceNum,
    invoice_type invoiceType,
    amount,
    tax,
    tax_include_amount taxIncludeAmount,
    construction_name constructionName,
    is_received isReceived,
    aging,
    receivable,
    received,
    unreceived
    from relocation_income ri
    left join unit u on ri.unit_id = u.id
    -- @where(){
      -- @if(cond.unitId == 429){
        and ri.unit_id in (SELECT id FROM unit WHERE parent_id = 429)
      -- @}
      -- @if(isNotEmpty(cond.unitId) && cond.unitId != 11 && cond.unitId != 429){
        and ri.unit_id = #{cond.unitId}
      -- @}
      -- @if(isNotEmpty(cond.contractDeadlineFrom)){
        and contract_deadline >= #{cond.contractDeadlineFrom}
      -- @}
      -- @if(isNotEmpty(cond.contractDeadlineTo)){
        and contract_deadline <= #{contractDeadlineTo}
      -- @}
      -- @if(isNotEmpty(cond.contractName)){
        and contract_name like concat('%', #{cond.contractName},'%')
      -- @}
      -- @if(isNotEmpty(cond.contractNum)){
        and contract_num like concat('%', #{cond.contractNum},'%')
      -- @}
      -- @if(isNotEmpty(cond.startTimeFrom)){
        and start_time >= #{cond.startTimeFrom}
      -- @}
      -- @if(isNotEmpty(cond.startTimeTo)){
        and start_time <= #{startTimeTo}
      -- @}
    -- @}
```

getMonthAmount
===
```sql
    select sum(amount)
        from relocation_income_detail
        where income_id = #{id} and pay_month = #{currentMonth}
```

selectProject
===
```sql
    SELECT project_id
    FROM relocation_invoice ri,
     relocation_income rii
    WHERE ri.invoice_number = #{invoiceNum}
    AND ri.invoice_number = rii.invoice_num;
```