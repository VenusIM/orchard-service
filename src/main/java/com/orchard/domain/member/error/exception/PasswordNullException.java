package com.orchard.domain.member.error.exception;

import com.orchard.global.error.exception.BusinessException;
import com.orchard.global.error.exception.ErrorCode;

public class PasswordNullException extends BusinessException {

    public PasswordNullException(ErrorCode errorCode) {
        super(errorCode);
    }
}
