package com.microservices.currencyexchangecervice.business;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonRootName(value = "Valute")
@JsonIgnoreProperties(value = {"Date", "name", "ID", "id"})
public class CurrencyValue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    @JacksonXmlElementWrapper(localName = "NumCode")
    int numCode;

    @JacksonXmlElementWrapper(localName = "CharCode")
    String charCode;

    @JacksonXmlElementWrapper(localName = "Nominal")
    int nominal;

    @JacksonXmlElementWrapper(localName = "Name")
    String currencyName;

    @JacksonXmlElementWrapper(localName = "Value")
    String value;
}
