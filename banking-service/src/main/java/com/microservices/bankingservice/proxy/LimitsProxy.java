package com.microservices.bankingservice.proxy;

import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "limits-service")
public interface LimitsProxy {
    Logger logger = LoggerFactory.getLogger(LimitsProxy.class);

    @GetMapping("/limits/{thing}/{attribute}")
    @Retry(name = "proxy", fallbackMethod = "fallback")
    Integer getLimits(@PathVariable String thing, @PathVariable String attribute);

    default String fallback() {
        logger.error("Limits service is down.");
        return "Some services are down. Try again later.";
    }
}
