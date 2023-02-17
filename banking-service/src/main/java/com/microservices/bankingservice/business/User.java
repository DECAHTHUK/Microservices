package com.microservices.bankingservice.business;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
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

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<Currency> wallet = new ArrayList<>();

    private double currentTransLimit;

    private double currentDollarConv;

    private double currentEuroConv;

    @OneToMany(mappedBy = "from", fetch = FetchType.LAZY)
    private List<Transaction> outgoingTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "to", fetch = FetchType.LAZY)
    private List<Transaction> incomingTransactions = new ArrayList<>();

    @CreationTimestamp
    private LocalDate lastTransaction;

    @CreationTimestamp
    private LocalDate lastDollarConv;

    @CreationTimestamp
    private LocalDate lastEuroConv;

    private LocalDate lastBonus;

    public User(long id) {
        this.id = id;
    }

    public User(long id, String username, double currentTransLimit, double currentDollarConv, double currentEuroConv) {
        this.id = id;
        this.username = username;
        this.currentTransLimit = currentTransLimit;
        this.currentDollarConv = currentDollarConv;
        this.currentEuroConv = currentEuroConv;
    }
}
