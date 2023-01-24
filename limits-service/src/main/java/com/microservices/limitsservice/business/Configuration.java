package com.microservices.limitsservice.business;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("limits-service")
@Data
public class Configuration {
    private int minimumTrans;

    private int maximumTrans;

    private int premiumMaximumTrans;

    private int dailyTransLimit;

    private int dollarConvDailyMaximum;

    private int premiumDollarConvDailyMaximum;

    private int euroConvDailyMaximum;

    private int premiumEuroConvDailyMaximum;

    private int dollarInAccountMaximum;

    private int euroInAccountMaximum;
}
