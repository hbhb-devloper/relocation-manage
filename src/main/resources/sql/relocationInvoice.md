selectListByCondition
===
```sql
    select
        -- @pageTag(){
        ri.id,
        ri.district           districtId,
        ri.unit_id            unitId,
        ri.invoice_code       invoiceCode,
        ri.invoice_number     invoiceNumber,
        ri.invoice_site       invoiceSite,
        ri.business_type      businessType,
        ri.invoice_type       invoiceType,
        ri.buyer_tax          buyerTax,
        ri.buyer_name         buyerName,
        ri.invoice_project    invoiceProject,
        ri.invoice_time       invoiceTime,
        ri.amount,
        ri.tax_rate           taxRate,
        ri.tax_amount         taxAmount,
        ri.tax_include_amount taxIncludeAmount,
        ri.remake,
        ri.applicant,
        ri.issuer,
        ri.state,
        ri.is_import          isImport,
        ri.manager
        -- @}
    from relocation_invoice ri
        -- @where(){
            -- @if(cond.unitId == 429){
                and ri.district in (#{cond.unitIds})
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
getInvoiceDetailById
===
```sql
    select
        ri.id,
        ri.district           districtId,
        ri.unit_id            unitId,
        ri.invoice_code       invoiceCode,
        ri.invoice_number     invoiceNumber,
        ri.invoice_site       invoiceSite,
        ri.business_type      businessType,
        ri.invoice_type       invoiceType,
        ri.buyer_tax          buyerTax,
        ri.buyer_name         buyerName,
        ri.invoice_project    invoiceProject,
        ri.invoice_time       invoiceTime,
        ri.amount,
        ri.tax_rate           taxRate,
        ri.tax_amount         taxAmount,
        ri.tax_include_amount taxIncludeAmount,
        ri.remake,
        ri.applicant,
        ri.issuer,
        ri.state,
        ri.is_import          isImport,
        ri.manager
    from relocation_invoice ri
        where ri.id = #{id}
```

updateByPrimaryKey
===
```sql
    update relocation_invoice set
    -- @trim(){
        -- @if(isNotEmpty(district)){
            district = #{cond.district},
        -- @}
        -- @if(isNotEmpty(unitId)){
            unit_id = #{cond.unitId},
        -- @}
        -- @if(isNotEmpty(invoiceCode)){
            invoice_code = #{cond.invoiceCode},
        -- @}
        -- @if(isNotEmpty(invoiceNumber)){
            invoice_number = #{cond.invoiceNumber},
        -- @}
        -- @if(isNotEmpty(invoiceSite)){
            invoice_site = #{cond.invoiceSite},
        -- @}
        -- @if(isNotEmpty(businessType)){
            business_type = #{cond.businessType},
        -- @}
        -- @if(isNotEmpty(invoiceType)){
            invoice_type = #{cond.invoiceType},
        -- @}
        -- @if(isNotEmpty(buyerTax)){
            buyer_tax = #{cond.buyerTax},
        -- @}
        -- @if(isNotEmpty(buyerName)){
            buyer_name = #{cond.buyerName},
        -- @}
        -- @if(isNotEmpty(invoiceProject)){
            invoice_project = #{cond.invoiceProject},
        -- @}
        -- @if(isNotEmpty(invoiceTime)){
            invoice_time = #{cond.invoiceTime},
        -- @}
        -- @if(isNotEmpty(amount)){
            amount = #{cond.amount},
        -- @}
        -- @if(isNotEmpty(taxRate)){
            tax_rate = #{cond.taxRate},
        -- @}
        -- @if(isNotEmpty(taxAmount)){
            tax_amount = #{cond.taxAmount}
        -- @}
        -- @if(isNotEmpty(taxIncludeAmount)){
            tax_include_amount = #{cond.taxIncludeAmount},
        -- @}
        -- @if(isNotEmpty(remake)){
            remake = #{cond.remake},
        -- @}
        -- @if(isNotEmpty(applicant)){
            applicant = #{cond.applicant},
        -- @}
        -- @if(isNotEmpty(issuer)){
            issuer = #{cond.issuer},
        -- @}
        -- @if(isNotEmpty(state)){
            state = #{cond.state},
        -- @}
        -- @if(isNotEmpty(isImport)){
            is_import = #{cond.isImport},
        -- @}
        -- @if(isNotEmpty(manager)){
            manager = #{cond.manager},
        -- @}
        -- @if(isNotEmpty(paymentType)){
            payment_type = #{cond.paymentType},
        -- @}
        -- @if(isNotEmpty(projectId)){
            project_id = #{cond.projectId},
        -- @}
    -- @}
        where id = #{cond.id}
```

getProjectInfo
===
```sql
    SELECT CONCAT(contract_num, unit_id, project_name) info,
    id FROM relocation_project
```

selectExportListByCondition
===
```sql
    select
        ri.id,
        ri.district           districtId,
        ri.unit_id            unitId,
        ri.invoice_code       invoiceCode,
        ri.invoice_number     invoiceNumber,
        ri.invoice_site       invoiceSite,
        ri.business_type      businessType,
        ri.invoice_type       invoiceType,
        ri.buyer_tax          buyerTax,
        ri.buyer_name         buyerName,
        ri.invoice_project    invoiceProject,
        ri.invoice_time       invoiceTime,
        ri.amount,
        ri.tax_rate           taxRate,
        ri.tax_amount         taxAmount,
        ri.tax_include_amount taxIncludeAmount,
        ri.remake,
        ri.applicant,
        ri.issuer,
        ri.state,
        ri.is_import          isImport,
        ri.manager,
        ri.project_state      projectState
    from relocation_invoice ri
        -- @where(){
          -- @if(cond.unitId == 429){
                and ri.district in (#{cond.unitIds})
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
```
selectPidByCondition
===
```sql
    select id
    from relocation_project
    where unit_id = #{unitId}
        and contract_num = #{contractNum}
        and project_name = #{pinfo}
```