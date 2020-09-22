sample
===
* 注释

	select #use("cols")# from relocation_income  where  #use("condition")#

cols
===
	id,category,unit_id,supplier,contract_num,contract_name,start_time,contract_deadline,contract_amount,invoice_time,invoice_num,invoice_type,amount,tax,tax_include_amount,construction_name,payment_type,is_received,aging,receivable,received,unreceived,receipt_num,payee

updateSample
===
	
	id=#id#,category=#category#,unit_id=#unitId#,supplier=#supplier#,contract_num=#contractNum#,contract_name=#contractName#,start_time=#startTime#,contract_deadline=#contractDeadline#,contract_amount=#contractAmount#,invoice_time=#invoiceTime#,invoice_num=#invoiceNum#,invoice_type=#invoiceType#,amount=#amount#,tax=#tax#,tax_include_amount=#taxIncludeAmount#,construction_name=#constructionName#,payment_type=#paymentType#,is_received=#isReceived#,aging=#aging#,receivable=#receivable#,received=#received#,unreceived=#unreceived#,receipt_num=#receiptNum#,payee=#payee#

condition
===

	1 = 1  
	@if(!isEmpty(id)){
	 and id=#id#
	@}
	@if(!isEmpty(category)){
	 and category=#category#
	@}
	@if(!isEmpty(unitId)){
	 and unit_id=#unitId#
	@}
	@if(!isEmpty(supplier)){
	 and supplier=#supplier#
	@}
	@if(!isEmpty(contractNum)){
	 and contract_num=#contractNum#
	@}
	@if(!isEmpty(contractName)){
	 and contract_name=#contractName#
	@}
	@if(!isEmpty(startTime)){
	 and start_time=#startTime#
	@}
	@if(!isEmpty(contractDeadline)){
	 and contract_deadline=#contractDeadline#
	@}
	@if(!isEmpty(contractAmount)){
	 and contract_amount=#contractAmount#
	@}
	@if(!isEmpty(invoiceTime)){
	 and invoice_time=#invoiceTime#
	@}
	@if(!isEmpty(invoiceNum)){
	 and invoice_num=#invoiceNum#
	@}
	@if(!isEmpty(invoiceType)){
	 and invoice_type=#invoiceType#
	@}
	@if(!isEmpty(amount)){
	 and amount=#amount#
	@}
	@if(!isEmpty(tax)){
	 and tax=#tax#
	@}
	@if(!isEmpty(taxIncludeAmount)){
	 and tax_include_amount=#taxIncludeAmount#
	@}
	@if(!isEmpty(constructionName)){
	 and construction_name=#constructionName#
	@}
	@if(!isEmpty(paymentType)){
	 and payment_type=#paymentType#
	@}
	@if(!isEmpty(isReceived)){
	 and is_received=#isReceived#
	@}
	@if(!isEmpty(aging)){
	 and aging=#aging#
	@}
	@if(!isEmpty(receivable)){
	 and receivable=#receivable#
	@}
	@if(!isEmpty(received)){
	 and received=#received#
	@}
	@if(!isEmpty(unreceived)){
	 and unreceived=#unreceived#
	@}
	@if(!isEmpty(receiptNum)){
	 and receipt_num=#receiptNum#
	@}
	@if(!isEmpty(payee)){
	 and payee=#payee#
	@}
	
	