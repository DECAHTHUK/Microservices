package com.microservices.currencyconverisonservice.proxy;

import com.microservices.currencyconverisonservice.Coefficient;
import com.microservices.currencyconverisonservice.proxy.handling.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="currency-exchange", configuration = FeignConfig.class)
public interface CurrencyExchangeProxy {
    @GetMapping("/currency-exchange/{from}/{to}")
    Coefficient retrieveExchangeValue(@PathVariable String from, @PathVariable String to);
}
