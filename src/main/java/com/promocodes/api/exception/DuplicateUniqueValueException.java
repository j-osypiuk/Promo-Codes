package com.promocodes.api.exception;

public class DuplicateUniqueValueException extends RuntimeException{

    public DuplicateUniqueValueException(String message) {
        super(message);
    }
}
