package com.microservices.currencyexchangecervice.presentation;

import com.microservices.currencyexchangecervice.business.Coefficient;
import com.microservices.currencyexchangecervice.business.CurrencyValue;
import com.microservices.currencyexchangecervice.business.CurrencyValueService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/currency-exchange")
public class CurrencyValueController {
    private final CurrencyValueService service;

    public CurrencyValueController(CurrencyValueService service) {
        this.service = service;
    }

    @GetMapping(value = "/{from}/{to}", produces =  MediaType.APPLICATION_JSON_VALUE)
    public Coefficient retrieveExchangeCoefficient(@PathVariable String from, @PathVariable String to) {
        return service.getCoefficient(from, to);
    }

    @GetMapping("/getAll")
    public List<CurrencyValue> getAllCurrencies() {
        return service.findAll();
    }
}
