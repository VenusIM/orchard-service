package com.orchard.domain.member.error.exception;

import com.orchard.global.error.exception.BusinessException;
import com.orchard.global.error.exception.ErrorCode;

public class DuplicateEmailException extends BusinessException {

    public DuplicateEmailException(ErrorCode errorCode) {
        super(errorCode);
    }
}
