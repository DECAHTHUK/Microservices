package com.microservices.currencyexchangecervice.business;

import com.microservices.currencyexchangecervice.ValuteCodeNotFoundException;
import com.microservices.currencyexchangecervice.persistence.CurrencyValueRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class CurrencyValueService {
    private final CurrencyValueRepository repository;

    private final Environment environment;

    @Autowired
    public CurrencyValueService(CurrencyValueRepository repository, Environment environment) {
        this.repository = repository;
        this.environment = environment;
    }

    public CurrencyValue findByCodes(String code) {
        CurrencyValue value;
        if (StringUtils.isNumeric(code)) {
            value = repository.findByNumCode(Integer.parseInt(code));

        } else {
            value = repository.findByCharCode(code);
        }
        if (value == null) {
            throw new ValuteCodeNotFoundException("Valute with the code " + code + " was not found");
        }
        return value;
    }

    public void save(CurrencyValue value) {
        repository.save(value);
    }


}
