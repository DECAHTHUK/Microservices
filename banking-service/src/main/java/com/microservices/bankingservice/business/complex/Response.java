package com.microservices.bankingservice.business.complex;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class Response {
    private HttpStatus status;

    private String description;

    private double newBalanceFrom;

    private double newBalanceTo;

    private String from;

    private String to;

    public Response(ResponseBuilder builder) {
        if (builder == null) {
            throw new IllegalArgumentException("Expected ResponseBuilder object");
        }
        if (builder.getStatus() == null) {
            throw new IllegalArgumentException("Status is obligatory!");
        }
        if (builder.getDescription() == null) {
            throw new IllegalArgumentException("Description is obligatory!");
        }
        this.status = builder.getStatus();
        this.description = builder.getDescription();
        this.newBalanceFrom = builder.getNewBalanceFrom();
        this.newBalanceTo = builder.getNewBalanceTo();
        this.from = builder.getFrom();
        this.to = builder.getTo();
    }
}
