package com.hbhb.cw.relocation.exception;

import com.hbhb.core.bean.MessageConvert;
import com.hbhb.cw.relocation.enums.InvoiceErrorCode;
import com.hbhb.web.exception.BusinessException;
import lombok.Getter;

/**
 * @author xiaokang
 * @since 2020-10-06
 */
@Getter
public class InvoiceException extends BusinessException {
    private static final long serialVersionUID = -9083303123097052232L;

    private final String code;

    public InvoiceException(InvoiceErrorCode errorCode) {
        super(errorCode.getCode(), MessageConvert.convert(errorCode.getMessage()));
        this.code = errorCode.getCode();
    }
}
