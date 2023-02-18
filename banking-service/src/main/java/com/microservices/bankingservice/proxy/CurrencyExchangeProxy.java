package com.microservices.bankingservice.proxy;

import com.microservices.bankingservice.business.CurrencyValue;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "currency-exchange")
public interface CurrencyExchangeProxy {
    Logger logger = LoggerFactory.getLogger(CurrencyExchangeProxy.class);

    @GetMapping("/currency-exchange/getAll")
    @Retry(name = "proxy", fallbackMethod = "fallback")
    List<CurrencyValue> getAllCurrencies();

    default String fallback() {
        logger.error("Currency exchange service is down.");
        return "Some services are down. Try again later.";
    }
}
