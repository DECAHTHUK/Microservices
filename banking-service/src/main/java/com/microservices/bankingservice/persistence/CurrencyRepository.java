package com.microservices.bankingservice.persistence;

import com.microservices.bankingservice.business.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    //TODO check indexesWorking, otherwise manual query
    Currency findByOwnerAndCharCode(long ownerId, String charCode);


}
