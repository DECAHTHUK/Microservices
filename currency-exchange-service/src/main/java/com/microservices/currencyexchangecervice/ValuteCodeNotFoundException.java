package com.microservices.currencyexchangecervice;

public class ValuteCodeNotFoundException extends RuntimeException {
    public ValuteCodeNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
