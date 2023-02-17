package com.microservices.bankingservice.persistence;

import com.microservices.bankingservice.business.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String name);

    User findUserById(long id);
}
