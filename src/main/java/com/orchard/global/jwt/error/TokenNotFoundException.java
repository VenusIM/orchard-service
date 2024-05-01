package com.orchard.global.jwt.error;

import com.orchard.global.error.exception.EntityNotFoundException;
import com.orchard.global.error.exception.ErrorCode;

public class TokenNotFoundException extends EntityNotFoundException {

    public TokenNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
