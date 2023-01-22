package com.microservices.bankingservice.business;

import lombok.*;

import javax.persistence.*;

@Entity(name = "currency")
@Table(name = "currencies",
        indexes = @Index(name = "wallIndex1", columnList = "owner_user, charCode", unique = true))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "owner_user")
    private User owner;

    @Column(name = "charCode")
    private String charCode;

    private double quantity;

}
