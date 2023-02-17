package com.microservices.currencyconverisonservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeProxy proxy;

    //TODO feign fallback(handling exceptions)
    @GetMapping("/currency-conversion-feign/{from}/{to}/{quantity}")
    public CurrencyConversion calculateCurrencyConversionFeign(@PathVariable String from,
                                                          @PathVariable String to,
                                                          @PathVariable double quantity) {
        Coefficient conversion = proxy.retrieveExchangeValue(from, to);
        double out;
        if (to.equals("BTC") || to.equals("ETH")) {
            out = (double)Math.round((quantity * conversion.getCoefficient()) * 1000000) / 1000000;
        } else {
            out = (double) Math.round((quantity * conversion.getCoefficient()) * 100) / 100;
        }
        return new CurrencyConversion(1, conversion.getFromCode(), conversion.getToCode(),
                conversion.getCoefficient(), quantity, out);
    }
}
