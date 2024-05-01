package com.orchard.domain.member.error.exception;

import com.orchard.global.error.exception.BusinessException;
import com.orchard.global.error.exception.ErrorCode;

public class PasswordMissMatchException extends BusinessException {

    public PasswordMissMatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
