package com.minhpt.smart_wallet_service.exception;


import com.minhpt.smart_wallet_service.util.MessageUtils;

public class OperationFindByIdNotAllowedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OperationFindByIdNotAllowedException() {
        super(MessageUtils.getMessage("method.not.allow"));
    }


}
