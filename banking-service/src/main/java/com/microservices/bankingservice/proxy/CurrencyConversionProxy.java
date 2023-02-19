package com.microservices.bankingservice.proxy;

import com.microservices.bankingservice.business.CurrencyConversion;
import com.microservices.bankingservice.proxy.handling.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "currency-conversion", configuration = FeignConfig.class)
public interface CurrencyConversionProxy {
    @GetMapping("/currency-conversion-feign/{from}/{to}/{quantity}")
    CurrencyConversion calculateCurrencyConversionFeign(@PathVariable String from,
                                                        @PathVariable String to,
                                                        @PathVariable double quantity);
}
