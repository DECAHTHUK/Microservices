package com.microservices.bankingservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("banking-service")
@Data
public class Configuration {
    private int bonusStandard;

    private int bonusPremium;
}
