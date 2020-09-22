sample
===
* 注释

	select #use("cols")# from relocation_receipt  where  #use("condition")#

cols
===
	id,category,unit_id,compensation_amount,payment_amount,contract_name,contract_num,payment_desc,receipt_amount,receipt_time,remake

updateSample
===
	
	id=#id#,category=#category#,unit_id=#unitId#,compensation_amount=#compensationAmount#,payment_amount=#paymentAmount#,contract_name=#contractName#,contract_num=#contractNum#,payment_desc=#paymentDesc#,receipt_amount=#receiptAmount#,receipt_time=#receiptTime#,remake=#remake#

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
	@if(!isEmpty(compensationAmount)){
	 and compensation_amount=#compensationAmount#
	@}
	@if(!isEmpty(paymentAmount)){
	 and payment_amount=#paymentAmount#
	@}
	@if(!isEmpty(contractName)){
	 and contract_name=#contractName#
	@}
	@if(!isEmpty(contractNum)){
	 and contract_num=#contractNum#
	@}
	@if(!isEmpty(paymentDesc)){
	 and payment_desc=#paymentDesc#
	@}
	@if(!isEmpty(receiptAmount)){
	 and receipt_amount=#receiptAmount#
	@}
	@if(!isEmpty(receiptTime)){
	 and receipt_time=#receiptTime#
	@}
	@if(!isEmpty(remake)){
	 and remake=#remake#
	@}
	
	