package com.microservices.currencyexchangecervice.business;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Coefficient {
    private String fromCode;

    private String toCode;

    private double coefficient;
}
