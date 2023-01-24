package com.microservices.bankingservice.presentation;


import com.microservices.bankingservice.business.Currency;
import com.microservices.bankingservice.business.complex.Response;
import com.microservices.bankingservice.persistence.service.BankingLogic;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.DecimalMin;
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

    @GetMapping(value = "/convert/from/{from}/to/{to}/quantity/{quantity}", produces = MediaType.APPLICATION_JSON)
    @Validated
    public Response convertValutes(@PathVariable String from,
                                   @PathVariable String to,
                                   @Digits(integer = 10, fraction = 2) @DecimalMin("1.00")
                                       @PathVariable double quantity,
                                   @AuthenticationPrincipal Jwt jwt) {
        return logic.convertValutes(from, to, quantity, jwt);
    }

    @GetMapping(value = "/test", produces = MediaType.APPLICATION_JSON)
    public Currency test(@AuthenticationPrincipal Jwt jwt) {
        return logic.getTest(jwt);
    }

}
