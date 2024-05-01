package com.orchard.global.jwt.error;

import com.orchard.global.error.exception.BusinessException;
import com.orchard.global.error.exception.ErrorCode;

public class UnAuthorizationException extends BusinessException {

    public UnAuthorizationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
