package com.microservices.bankingservice.business;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "user")
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String username;

    @OneToMany(mappedBy = "owner")
    private List<Currency> wallet = new ArrayList<>();

    private double currentLimit;

    @OneToMany(mappedBy = "from")
    private List<Transaction> outgoingTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "to")
    private List<Transaction> incomingTransactions = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime lastTransaction;

    public User(long id) {
        this.id = id;
    }
}
