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
            -- @if(!isEmpty(amountFrom)){
                and ri.amount >= #{amountFrom}
            -- @}
            -- @if(!isEmpty(amountTo)){
                and ri.amount <= #{amountTo}
            -- @}
            -- @if(!isEmpty(invoiceTimeFrom)){
                and ri.invoice_time >= #{invoiceTimeFrom}
            -- @}
            -- @if(!isEmpty(invoiceTimeTo)){
                and ri.invoice_time <= #{invoiceTimeTo}
            -- @}
            -- @if(unitId == 429){
                and ri.district in (SELECT id FROM unit WHERE parent_id = 429)
            -- @}
            -- @if(unitId != 11 && unitId != 429){
                and ri.district = #{unitId}
            -- @}
        -- @}
    -- @pageIgnoreTag(){
        order by ri.id desc
    -- @}
```
