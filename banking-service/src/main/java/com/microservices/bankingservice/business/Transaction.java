package com.microservices.bankingservice.business;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "transaction")
@Table(name = "transactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "from_user")
    private User from;

    @ManyToOne
    @JoinColumn(name = "to_user")
    private User to;

    private String message;

    private String charCode;

    private double quantity;

    @CreationTimestamp
    private LocalDateTime time;

}
