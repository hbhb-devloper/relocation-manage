sample
===
* 注释

	select #use("cols")# from relocation_income_detail  where  #use("condition")#

cols
===
	id,income_id,amount,receipt_num,pay_month,payee,create_time

updateSample
===
	
	id=#id#,income_id=#incomeId#,amount=#amount#,receipt_num=#receiptNum#,pay_month=#payMonth#,payee=#payee#,create_time=#createTime#

condition
===

	1 = 1  
	@if(!isEmpty(id)){
	 and id=#id#
	@}
	@if(!isEmpty(incomeId)){
	 and income_id=#incomeId#
	@}
	@if(!isEmpty(amount)){
	 and amount=#amount#
	@}
	@if(!isEmpty(receiptNum)){
	 and receipt_num=#receiptNum#
	@}
	@if(!isEmpty(payMonth)){
	 and pay_month=#payMonth#
	@}
	@if(!isEmpty(payee)){
	 and payee=#payee#
	@}
	@if(!isEmpty(createTime)){
	 and create_time=#createTime#
	@}
	
	