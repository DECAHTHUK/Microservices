package com.microservices.bankingservice.proxy;

import com.microservices.bankingservice.business.CurrencyConversion;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "currency-conversion")
public interface CurrencyConversionProxy {
    Logger logger = LoggerFactory.getLogger(CurrencyConversionProxy.class);

    @GetMapping("/currency-conversion-feign/{from}/{to}/{quantity}")
    @Retry(name = "proxy", fallbackMethod = "fallback")
    CurrencyConversion calculateCurrencyConversionFeign(@PathVariable String from,
                                                        @PathVariable String to,
                                                        @PathVariable double quantity);

    default String fallback() {
        logger.error("Currency conversion service is down.");
        return "Some services are down. Try again later.";
    }
}
