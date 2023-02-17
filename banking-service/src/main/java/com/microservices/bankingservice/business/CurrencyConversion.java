package com.microservices.bankingservice.business;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CurrencyConversion {

    private long id;

    private String from;

    private String to;

    private double conversionMultiple;

    private double quantity;

    private double totalCalculatedAmount;

}
