package com.microservices.bankingservice.proxy;

import com.microservices.bankingservice.business.CurrencyValue;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "currency-exchange")
public interface CurrencyExchangeProxy {
    @GetMapping("/currency-exchange/getAll")
    List<CurrencyValue> getAllCurrencies();
}
