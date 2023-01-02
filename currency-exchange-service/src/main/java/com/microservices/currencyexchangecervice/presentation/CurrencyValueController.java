package com.microservices.currencyexchangecervice.presentation;

import com.microservices.currencyexchangecervice.business.CurrencyValue;
import com.microservices.currencyexchangecervice.business.CurrencyValueService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CurrencyValueController {
    private final CurrencyValueService service;

    public CurrencyValueController(CurrencyValueService service) {
        this.service = service;
    }

    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public CurrencyValue retrieveExchangeValue(@PathVariable String from, @PathVariable String to) {

        return null;
    }
}
