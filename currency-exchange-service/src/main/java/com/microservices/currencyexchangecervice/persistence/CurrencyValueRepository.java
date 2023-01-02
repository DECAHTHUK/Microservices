package com.microservices.currencyexchangecervice.persistence;

import com.microservices.currencyexchangecervice.business.CurrencyValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyValueRepository extends JpaRepository<CurrencyValue, Long> {
    CurrencyValue findByCharCode(String code);

    CurrencyValue findByNumCode(int code);

    List<CurrencyValue> findAll();
}
