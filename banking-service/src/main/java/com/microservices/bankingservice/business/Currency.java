package com.microservices.bankingservice.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user")
    @JsonIgnore
    private User owner;

    @Column(name = "charCode")
    private String charCode;

    private double quantity;

}
