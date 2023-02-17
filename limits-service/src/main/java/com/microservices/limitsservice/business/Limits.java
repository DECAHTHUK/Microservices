package com.microservices.limitsservice.business;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Limits {
    private int minimumTrans;

    private int maximumTrans;

    private int premiumMaximumTrans;

    private int dollarInAccountMaximum;

    private int premiumDollarInAccountMaximum;

    private int euroInAccountMaximum;

    private int premiumEuroInAccountMaximum;
}
