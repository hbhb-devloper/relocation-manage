sample
===
* 注释

	select #use("cols")# from relocation_project  where  #use("condition")#

cols
===
	id,unit_id,project_name,project_num,project_type,project_year,project_month,eoms_repair_num,eoms_cut_num,plan_start_time,plan_end_time,actual_end_time,network_hierarchy,construction_budget,construction_cost,construction_audit_cost,construction_unit,material_budget,material_cost,opposite_unit,opposite_contacts,opposite_contacts_num,has_compensation,compensation_type,compensation_amount,compensation_sate,compensation_remake,contract_num,contract_type,contract_name,contract_duration,is_initiative,anticipate_payable,anticipate_payment,final_payment,cause

updateSample
===
	
	id=#id#,unit_id=#unitId#,project_name=#projectName#,project_num=#projectNum#,project_type=#projectType#,project_year=#projectYear#,project_month=#projectMonth#,eoms_repair_num=#eomsRepairNum#,eoms_cut_num=#eomsCutNum#,plan_start_time=#planStartTime#,plan_end_time=#planEndTime#,actual_end_time=#actualEndTime#,network_hierarchy=#networkHierarchy#,construction_budget=#constructionBudget#,construction_cost=#constructionCost#,construction_audit_cost=#constructionAuditCost#,construction_unit=#constructionUnit#,material_budget=#materialBudget#,material_cost=#materialCost#,opposite_unit=#oppositeUnit#,opposite_contacts=#oppositeContacts#,opposite_contacts_num=#oppositeContactsNum#,has_compensation=#hasCompensation#,compensation_type=#compensationType#,compensation_amount=#compensationAmount#,compensation_sate=#compensationSate#,compensation_remake=#compensationRemake#,contract_num=#contractNum#,contract_type=#contractType#,contract_name=#contractName#,contract_duration=#contractDuration#,is_initiative=#isInitiative#,anticipate_payable=#anticipatePayable#,anticipate_payment=#anticipatePayment#,final_payment=#finalPayment#,cause=#cause#

condition
===

	1 = 1  
	@if(!isEmpty(id)){
	 and id=#id#
	@}
	@if(!isEmpty(unitId)){
	 and unit_id=#unitId#
	@}
	@if(!isEmpty(projectName)){
	 and project_name=#projectName#
	@}
	@if(!isEmpty(projectNum)){
	 and project_num=#projectNum#
	@}
	@if(!isEmpty(projectType)){
	 and project_type=#projectType#
	@}
	@if(!isEmpty(projectYear)){
	 and project_year=#projectYear#
	@}
	@if(!isEmpty(projectMonth)){
	 and project_month=#projectMonth#
	@}
	@if(!isEmpty(eomsRepairNum)){
	 and eoms_repair_num=#eomsRepairNum#
	@}
	@if(!isEmpty(eomsCutNum)){
	 and eoms_cut_num=#eomsCutNum#
	@}
	@if(!isEmpty(planStartTime)){
	 and plan_start_time=#planStartTime#
	@}
	@if(!isEmpty(planEndTime)){
	 and plan_end_time=#planEndTime#
	@}
	@if(!isEmpty(actualEndTime)){
	 and actual_end_time=#actualEndTime#
	@}
	@if(!isEmpty(networkHierarchy)){
	 and network_hierarchy=#networkHierarchy#
	@}
	@if(!isEmpty(constructionBudget)){
	 and construction_budget=#constructionBudget#
	@}
	@if(!isEmpty(constructionCost)){
	 and construction_cost=#constructionCost#
	@}
	@if(!isEmpty(constructionAuditCost)){
	 and construction_audit_cost=#constructionAuditCost#
	@}
	@if(!isEmpty(constructionUnit)){
	 and construction_unit=#constructionUnit#
	@}
	@if(!isEmpty(materialBudget)){
	 and material_budget=#materialBudget#
	@}
	@if(!isEmpty(materialCost)){
	 and material_cost=#materialCost#
	@}
	@if(!isEmpty(oppositeUnit)){
	 and opposite_unit=#oppositeUnit#
	@}
	@if(!isEmpty(oppositeContacts)){
	 and opposite_contacts=#oppositeContacts#
	@}
	@if(!isEmpty(oppositeContactsNum)){
	 and opposite_contacts_num=#oppositeContactsNum#
	@}
	@if(!isEmpty(hasCompensation)){
	 and has_compensation=#hasCompensation#
	@}
	@if(!isEmpty(compensationType)){
	 and compensation_type=#compensationType#
	@}
	@if(!isEmpty(compensationAmount)){
	 and compensation_amount=#compensationAmount#
	@}
	@if(!isEmpty(compensationSate)){
	 and compensation_sate=#compensationSate#
	@}
	@if(!isEmpty(compensationRemake)){
	 and compensation_remake=#compensationRemake#
	@}
	@if(!isEmpty(contractNum)){
	 and contract_num=#contractNum#
	@}
	@if(!isEmpty(contractType)){
	 and contract_type=#contractType#
	@}
	@if(!isEmpty(contractName)){
	 and contract_name=#contractName#
	@}
	@if(!isEmpty(contractDuration)){
	 and contract_duration=#contractDuration#
	@}
	@if(!isEmpty(isInitiative)){
	 and is_initiative=#isInitiative#
	@}
	@if(!isEmpty(anticipatePayable)){
	 and anticipate_payable=#anticipatePayable#
	@}
	@if(!isEmpty(anticipatePayment)){
	 and anticipate_payment=#anticipatePayment#
	@}
	@if(!isEmpty(finalPayment)){
	 and final_payment=#finalPayment#
	@}
	@if(!isEmpty(cause)){
	 and cause=#cause#
	@}
	
	