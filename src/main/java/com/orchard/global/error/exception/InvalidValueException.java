package com.orchard.global.error.exception;

// InvalidException Root
public class InvalidValueException extends BusinessException {

    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }
}
