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

    private double conversionMultiple;

    private BigDecimal quantity;

    private BigDecimal totalCalculatedAmount;
}
