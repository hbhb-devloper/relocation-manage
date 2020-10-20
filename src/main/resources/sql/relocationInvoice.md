selectListByCondition
===
```sql
    select
        -- @pageTag(){
        ri.id,
        u1.unit_name district,
        u.unit_name unit,
        ri.invoice_code invoiceCode,
        ri.invoice_number invoiceNumber,
        ri.invoice_site invoiceSite,
        ri.business_type businessType,
        ri.invoice_type invoiceType,
        ri.buyer_tax buyerTax,
        ri.buyer_name buyerName,
        ri.invoice_project invoiceProject,
        ri.invoice_time invoiceTime,
        ri.amount,
        ri.tax_rate taxRate,
        ri.tax_amount taxAmount,
        ri.tax_include_amount taxIncludeAmount,
        ri.remake,
        ri.applicant,
        ri.issuer,
        ri.state,
        ri.is_import isImport,
        ri.manager
        -- @}
        from relocation_invoice ri
        left join unit u on ri.unit_id = u.id
        left join unit u1 on u1.id = ri.district
        -- @where(){
            -- @if(cond.unitId == 429){
                and ri.district in (SELECT id FROM unit WHERE parent_id = 429)
            -- @}
            -- @if(cond.unitId != 11 && cond.unitId != 429){
                and ri.district = #{cond.unitId}
            -- @}
            -- @if(isNotEmpty(cond.amountFrom)){
                and ri.amount >= #{cond.amountFrom}
            -- @}
            -- @if(isNotEmpty(cond.amountTo)){
                and ri.amount <= #{cond.amountTo}
            -- @}
            -- @if(isNotEmpty(cond.invoiceTimeFrom)){
                and ri.invoice_time >= #{cond.invoiceTimeFrom}
            -- @}
            -- @if(isNotEmpty(cond.invoiceTimeTo)){
                and ri.invoice_time <= #{cond.invoiceTimeTo}
            -- @}
        -- @}
    -- @pageIgnoreTag(){
        order by ri.id desc
    -- @}
```
