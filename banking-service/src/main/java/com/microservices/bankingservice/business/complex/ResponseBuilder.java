package com.microservices.bankingservice.business.complex;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class ResponseBuilder {
    private HttpStatus status;

    private String description;

    private double newBalanceFrom;

    private double newBalanceTo;

    private String from;

    private String to;

    public ResponseBuilder() {
        super();
    }

    public ResponseBuilder status(HttpStatus status) {
        this.status = status;
        return this;
    }

    public ResponseBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ResponseBuilder newBalanceFrom(double newBalanceFrom) {
        this.newBalanceFrom = newBalanceFrom;
        return this;
    }

    public ResponseBuilder newBalanceTo(double newBalanceTo) {
        this.newBalanceTo = newBalanceTo;
        return this;
    }

    public ResponseBuilder from(String from) {
        this.from = from;
        return this;
    }

    public ResponseBuilder to(String to) {
        this.to = to;
        return this;
    }

    public Response build() {
        return new Response(this);
    }
}
