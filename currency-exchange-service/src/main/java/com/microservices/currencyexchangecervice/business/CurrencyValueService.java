package com.microservices.currencyexchangecervice.business;

import com.microservices.currencyexchangecervice.persistence.CurrencyValueRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

@Service
public class CurrencyValueService {
    @Autowired
    CurrencyValueRepository repository;

    @Autowired
    Environment environment;


    public CurrencyValue findByCodes(String code) {
        CurrencyValue value;
        if (StringUtils.isNumeric(code)) {
            value = repository.findByNumCode(Integer.parseInt(code));

        } else {
            value = repository.findByCharCode(code);
        }
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Valute with the code " + code + " was not found");
        }
        return value;
    }

    public Coefficient getCoefficient(String from, String to) {
        CurrencyValue val1 = findByCodes(from);
        CurrencyValue val2 = findByCodes(to);

        NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);

        double coef1;
        double coef2;
        try {
            coef1 = (nf.parse(val1.getPower())).doubleValue() / val1.getNominal();
            coef2 = (nf.parse(val2.getPower())).doubleValue() / val2.getNominal();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        double out = (double)Math.round((coef1 / coef2) * 10000) / 10000;
        return new Coefficient(val1.getCharCode(), val2.getCharCode(), out);
    }

    public void save(CurrencyValue value) {
        repository.save(value);
    }


}