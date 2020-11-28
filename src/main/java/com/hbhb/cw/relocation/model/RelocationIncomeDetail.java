package com.hbhb.cw.relocation.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beetl.sql.annotation.entity.AutoID;

/**
 * @since 2020-09-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelocationIncomeDetail implements Serializable {

    private static final long serialVersionUID = -7894679294000895303L;

    @Schema(description = "id")
    @AutoID
    private Long id;
	/**
	 * 收款id
	 */
    @Schema(description = "收款数据id")
	private Long incomeId;
	/**
	 * 每次收款的金额
	 */
    @Schema(description = "本次收款金额")
	private BigDecimal amount;
	/**
	 * 收款单号
	 */
    @Schema(description = "收款单号")
	private String receiptNum;
	/**
	 * 收款月份
	 */
    @Schema(description = "收款月份")
	private String payMonth;
	/**
	 * 收款人
	 */
    @Schema(description = "收款人")
	private String payee;
	/**
	 * 创建时间
	 */
    @Schema(description = "创建时间")
	private String createTime;
}
