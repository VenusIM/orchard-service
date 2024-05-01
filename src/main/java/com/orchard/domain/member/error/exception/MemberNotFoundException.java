package com.orchard.domain.member.error.exception;

import com.orchard.global.error.exception.EntityNotFoundException;
import com.orchard.global.error.exception.ErrorCode;

public class MemberNotFoundException extends EntityNotFoundException {

    public MemberNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
