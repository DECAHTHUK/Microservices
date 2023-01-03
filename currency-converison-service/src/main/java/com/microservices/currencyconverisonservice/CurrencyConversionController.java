package com.microservices.currencyconverisonservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeProxy proxy;

    //TODO feign fallback(handling exceptions)
    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversionFeign(@PathVariable String from,
                                                          @PathVariable String to,
                                                          @PathVariable BigDecimal quantity) {
        Coefficient conversion = proxy.retrieveExchangeValue(from, to);

        return new CurrencyConversion(1, conversion.getFromCode(), conversion.getToCode(),
                conversion.getCoefficient(), quantity, quantity.multiply(BigDecimal.valueOf(conversion.getCoefficient())));
    }
}
