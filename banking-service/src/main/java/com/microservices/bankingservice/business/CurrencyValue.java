package com.microservices.bankingservice.business;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CurrencyValue {
    private long id;

    private int numCode;

    private String charCode;

    private int nominal;

    private String currencyName;

    private String power;
}
