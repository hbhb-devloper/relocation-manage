sample
===
* 注释

	select #use("cols")# from relocation_invoice  where  #use("condition")#

cols
===
	id,district,unit_id,invoice_code,invoice_number,invoice_site,business_type,invoice_type,buyer_tax,buyer_name,invoice_project,invoice_time,amount,tax_rate,tax_amount,tax_include_amount,remake,applicant,issuer,state,is_import,manager

updateSample
===
	id=#id#,district=#district#,unit_id=#unitId#,invoice_code=#invoiceCode#,invoice_number=#invoiceNumber#,invoice_site=#invoiceSite#,business_type=#businessType#,invoice_type=#invoiceType#,buyer_tax=#buyerTax#,buyer_name=#buyerName#,invoice_project=#invoiceProject#,invoice_time=#invoiceTime#,amount=#amount#,tax_rate=#taxRate#,tax_amount=#taxAmount#,tax_include_amount=#taxIncludeAmount#,remake=#remake#,applicant=#applicant#,issuer=#issuer#,state=#state#,is_import=#isImport#,manager=#manager#

condition
===
	1 = 1  
	@if(!isEmpty(id)){
	 and id=#id#
	@}
	@if(!isEmpty(district)){
	 and district=#district#
	@}
	@if(!isEmpty(unitId)){
	 and unit_id=#unitId#
	@}
	@if(!isEmpty(invoiceCode)){
	 and invoice_code=#invoiceCode#
	@}
	@if(!isEmpty(invoiceNumber)){
	 and invoice_number=#invoiceNumber#
	@}
	@if(!isEmpty(invoiceSite)){
	 and invoice_site=#invoiceSite#
	@}
	@if(!isEmpty(businessType)){
	 and business_type=#businessType#
	@}
	@if(!isEmpty(invoiceType)){
	 and invoice_type=#invoiceType#
	@}
	@if(!isEmpty(buyerTax)){
	 and buyer_tax=#buyerTax#
	@}
	@if(!isEmpty(buyerName)){
	 and buyer_name=#buyerName#
	@}
	@if(!isEmpty(invoiceProject)){
	 and invoice_project=#invoiceProject#
	@}
	@if(!isEmpty(invoiceTime)){
	 and invoice_time=#invoiceTime#
	@}
	@if(!isEmpty(amount)){
	 and amount=#amount#
	@}
	@if(!isEmpty(taxRate)){
	 and tax_rate=#taxRate#
	@}
	@if(!isEmpty(taxAmount)){
	 and tax_amount=#taxAmount#
	@}
	@if(!isEmpty(taxIncludeAmount)){
	 and tax_include_amount=#taxIncludeAmount#
	@}
	@if(!isEmpty(remake)){
	 and remake=#remake#
	@}
	@if(!isEmpty(applicant)){
	 and applicant=#applicant#
	@}
	@if(!isEmpty(issuer)){
	 and issuer=#issuer#
	@}
	@if(!isEmpty(state)){
	 and state=#state#
	@}
	@if(!isEmpty(isImport)){
	 and is_import=#isImport#
	@}
	@if(!isEmpty(manager)){
	 and manager=#manager#
	@}
	