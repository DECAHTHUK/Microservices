package com.microservices.bankingservice.persistence.service;

import com.microservices.bankingservice.business.User;
import com.microservices.bankingservice.persistence.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User findByUsername(String username) {
        return repository.findUserByUsername(username);
    }

    public long createNewUser(String username) {
        User user = new User(0, username, null, 0,
                null, null, null);
        return save(user);
    }


    public long save(User user) {
        return repository.save(user).getId();
    }
}
