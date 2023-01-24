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

    public User findById(long id) { return repository.findUserById(id);}

    public long createNewUser(String username) {
        User user = new User(0, username, 0, 0, 0);
        return save(user);
    }


    public long save(User user) {
        return repository.save(user).getId();
    }
}
