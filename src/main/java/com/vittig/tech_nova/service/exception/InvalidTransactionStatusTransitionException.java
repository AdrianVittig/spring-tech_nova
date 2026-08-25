package com.vittig.tech_nova.service.exception;

public class InvalidTransactionStatusTransitionException extends RuntimeException {
    public InvalidTransactionStatusTransitionException(String message) {
        super(message);
    }
}
