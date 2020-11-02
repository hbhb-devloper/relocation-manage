package com.hbhb.cw.relocation.exception;

import com.hbhb.core.bean.MessageConvert;
import com.hbhb.cw.relocation.enums.RelocationErrorCode;
import com.hbhb.web.exception.BusinessException;
import lombok.Getter;

@Getter
public class RelocationException extends BusinessException {


    private final String code;

    public RelocationException(RelocationErrorCode errorCode) {
        super(errorCode.getCode(), MessageConvert.convert(errorCode.getMessage()));
        this.code = errorCode.getCode();
    }

    public RelocationException(RelocationErrorCode errorCode, String msg) {
        super(errorCode.getCode(), msg);
        this.code = errorCode.getCode();
    }


}
