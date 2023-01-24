package com.microservices.currencyconverisonservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyConversion {

    private long id;

    private String from;

    private String to;

    private double conversionMultiple;

    private double quantity;

    private double totalCalculatedAmount;
}
