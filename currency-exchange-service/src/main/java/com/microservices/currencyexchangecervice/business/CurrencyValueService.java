package com.microservices.currencyexchangecervice.business;

import com.microservices.currencyexchangecervice.CircuitBreakerController;
import com.microservices.currencyexchangecervice.persistence.CurrencyValueRepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class CurrencyValueService {
    private final Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);
    @Autowired
    CurrencyValueRepository repository;


    public CurrencyValue findByCodes(String code) {
        CurrencyValue value;
        if (StringUtils.isNumeric(code)) {
            value = repository.findByNumCode(Integer.parseInt(code));

        } else {
            value = repository.findByCharCode(code);
        }
        if (value == null) {
            logger.info("Valute with the code " + code + " was not found");
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
            logger.error("Error while parsing coefficient: " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }

        double out = (double)Math.round((coef1 / coef2) * 10000000) / 10000000;
        return new Coefficient(val1.getCharCode(), val2.getCharCode(), out);
    }

    public void save(CurrencyValue value) {
        CurrencyValue currencyValue = repository.findByCharCode(value.getCharCode());
        if (currencyValue != null) {
            value.setId(currencyValue.getId());
        }
        repository.save(value);
    }

    public void saveCrypto(CurrencyValue value, double coefficient) {

        String newPower = String.valueOf((double)Math.round
                ((Double.parseDouble(value.getPower()) * coefficient) * 10000) / 10000);

        newPower = newPower.replaceAll("\\.", ",");

        value.setPower(newPower);
        save(value);
    }

    public List<CurrencyValue> findAll() {
        return repository.findAll();
    }


}
