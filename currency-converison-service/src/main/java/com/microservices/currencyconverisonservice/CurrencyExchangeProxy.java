package com.microservices.currencyconverisonservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name="currency-exchange", url="localhost:8000")
@FeignClient(name="currency-exchange") //it will talk with eureka, not using some specific urls
public interface CurrencyExchangeProxy {
    @GetMapping("/currency-exchange/{from}/{to}")
    Coefficient retrieveExchangeValue(@PathVariable String from, @PathVariable String to);
}
