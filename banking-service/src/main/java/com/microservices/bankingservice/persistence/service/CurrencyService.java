package com.microservices.bankingservice.persistence.service;

import com.microservices.bankingservice.business.Currency;
import com.microservices.bankingservice.business.User;
import com.microservices.bankingservice.persistence.CurrencyRepository;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService {

    private final CurrencyRepository repository;

    public CurrencyService(CurrencyRepository repository) {
        this.repository = repository;
    }

    public Currency findByOwnerAndCharCode(long ownerId, String charCode) {
        return repository.findByOwnerAndCharCode(ownerId, charCode);
    }

    public void updateCurrency(long userId, String charCode, double quantity) {
        Currency currency = findByOwnerAndCharCode(userId, charCode);
        if (currency != null) {
            currency.setQuantity(currency.getQuantity() + quantity);
        } else {
            currency = new Currency(0, new User(userId), charCode, quantity);
        }
        save(currency);
    }

    public void save(Currency currency) {
        repository.save(currency);
    }
}
