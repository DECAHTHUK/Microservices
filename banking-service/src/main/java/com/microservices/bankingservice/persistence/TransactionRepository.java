package com.microservices.bankingservice.persistence;

import com.microservices.bankingservice.business.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(value = "Select * from transactions where from_user = ?1", nativeQuery = true)
    List<Transaction> findAllByTo(long id);
}
