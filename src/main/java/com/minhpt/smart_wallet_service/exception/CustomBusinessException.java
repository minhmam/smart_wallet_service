package com.minhpt.smart_wallet_service.exception;

import com.minhpt.smart_wallet_service.util.ExceptionUtil;
import com.minhpt.smart_wallet_service.util.MessageUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomBusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String message;
    private final int code;

    public CustomBusinessException(String errorCode, String... args) {
        this.code = ExceptionUtil.getCodeError(errorCode);
        this.message = MessageUtils.getMessage(ExceptionUtil.getMessageError(errorCode)+args[0]);
    }

    public CustomBusinessException(String errorCode) {
        this.code = ExceptionUtil.getCodeError(errorCode);
        this.message = MessageUtils.getMessage(ExceptionUtil.getMessageError(errorCode));
    }


}
