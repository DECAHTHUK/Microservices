package com.microservices.bankingservice.presentation;


import com.microservices.bankingservice.business.Currency;
import com.microservices.bankingservice.business.CurrencyValue;
import com.microservices.bankingservice.business.Transaction;
import com.microservices.bankingservice.business.complex.Response;
import com.microservices.bankingservice.persistence.service.BankingLogic;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Digits;
import javax.ws.rs.core.MediaType;
import java.util.List;

@RestController
@RequestMapping("/banking")
public class BankingController {
    private final BankingLogic logic;

    public BankingController(BankingLogic logic) {
        this.logic = logic;
    }

    @GetMapping("/get-starter-bonus")
    public String getStarterPack(@AuthenticationPrincipal Jwt jwt) {
        return logic.getStarterPack(jwt);
    }

    @GetMapping("/wallet")
    public List<Currency> getWallet(@AuthenticationPrincipal Jwt jwt) {return logic.getWallet(jwt);}

    @GetMapping(value = "/convert/{from}/{to}/{quantity}", produces = MediaType.APPLICATION_JSON)
    @Validated
    public Response convertValutes(@PathVariable String from,
                                   @PathVariable String to,
                                   @Digits(integer = 10, fraction = 5)
                                       @PathVariable double quantity,
                                   @AuthenticationPrincipal Jwt jwt) {
        return logic.convertValutes(from, to, quantity, jwt);
    }

    @GetMapping(value = "/transfer/{to}/{code}/{quantity}", produces = MediaType.APPLICATION_JSON)
    @Validated
    public Response transferValute(@PathVariable String to,
                                   @PathVariable String code,
                                   @Digits(integer = 10, fraction = 5)
                                       @PathVariable double quantity,
                                   @AuthenticationPrincipal Jwt jwt) {
        return logic.transferValutes(to, code, quantity, jwt);
    }

    @GetMapping(value = "/transactions", produces = MediaType.APPLICATION_JSON)
    public List<Transaction> getTransactions(@AuthenticationPrincipal Jwt jwt) {
        return logic.findAllTransactions(jwt);
    }

    @GetMapping(value = "/get-course", produces = MediaType.APPLICATION_JSON)
    public List<CurrencyValue> getCource() {
        return logic.getAllCurrencies();
    }

}
