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
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;

    @JacksonXmlElementWrapper(localName = "NumCode")
    private int numCode;

    @JacksonXmlElementWrapper(localName = "CharCode")
    private String charCode;

    @JacksonXmlElementWrapper(localName = "Nominal")
    private int nominal;

    @JacksonXmlElementWrapper(localName = "Name")
    private String currencyName;

    @JacksonXmlElementWrapper(localName = "Value")
    private String power;

    public CurrencyValue(int numCode, String charCode, int nominal, String power) {
        this.numCode = numCode;
        this.charCode = charCode;
        this.nominal = nominal;
        this.power = power;
    }

    public CurrencyValue(int numCode, String charCode, int nominal, String currencyName, String power) {
        this.numCode = numCode;
        this.charCode = charCode;
        this.nominal = nominal;
        this.currencyName = currencyName;
        this.power = power;
    }
}
