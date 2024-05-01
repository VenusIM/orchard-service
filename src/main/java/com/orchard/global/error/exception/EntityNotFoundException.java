package com.orchard.global.error.exception;

// Not Found Exception Root
public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
