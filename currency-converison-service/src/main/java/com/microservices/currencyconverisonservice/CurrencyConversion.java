package com.microservices.currencyconverisonservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyConversion {

    private long id;

    private String from;

    private String to;

    private BigDecimal conversionMultiple;

    private BigDecimal quantity;

    private BigDecimal totalCalculatedAmount;

    private String environment;

    public CurrencyConversion(long id, String from, String to, BigDecimal quantity) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.quantity = quantity;
    }
}
