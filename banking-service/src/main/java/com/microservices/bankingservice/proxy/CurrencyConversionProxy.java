package com.microservices.bankingservice.proxy;

import com.microservices.bankingservice.business.CurrencyConversion;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "currency-conversion")
public interface CurrencyConversionProxy {
    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    CurrencyConversion calculateCurrencyConversionFeign(@PathVariable String from,
                                                        @PathVariable String to,
                                                        @PathVariable double quantity);
}
