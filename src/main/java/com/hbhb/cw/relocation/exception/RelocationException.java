package com.hbhb.cw.relocation.exception;

import com.hbhb.core.bean.MessageConvert;
import com.hbhb.cw.relocation.enums.RelocationErrorCode;
import com.hbhb.web.exception.BusinessException;
import lombok.Getter;

/**
 * @author wangxiaogang
 */
@Getter
public class RelocationException extends BusinessException {


    private static final long serialVersionUID = 607151396907007963L;
    private final String code;

    public RelocationException(RelocationErrorCode errorCode) {
        super(errorCode.getCode(), MessageConvert.convert(errorCode.getMessage()));
        this.code = errorCode.getCode();
    }

    public RelocationException(String code, String msg) {
        super(code, msg);
        this.code = code;
    }


}
