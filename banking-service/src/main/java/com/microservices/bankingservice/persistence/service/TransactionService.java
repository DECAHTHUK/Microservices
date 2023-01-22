package com.microservices.bankingservice.persistence.service;

import com.microservices.bankingservice.business.Transaction;
import com.microservices.bankingservice.persistence.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public void save(Transaction transaction) {
        repository.save(transaction);
    }
}
