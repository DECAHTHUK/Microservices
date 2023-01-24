package com.microservices.bankingservice.persistence;

import com.microservices.bankingservice.business.Currency;
import com.microservices.bankingservice.business.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    //TODO check indexesWorking, otherwise manual query (actually they are not, need manual)
    Currency findByOwnerAndCharCode(User owner, String charCode);

    List<Currency> findAllByOwner(User owner);
}
