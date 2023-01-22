package com.microservices.bankingservice.presentation;


import com.microservices.bankingservice.persistence.service.BankingLogic;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/banking")
public class BankingController {
    private final BankingLogic logic;

    public BankingController(BankingLogic logic) {
        this.logic = logic;
    }

    @GetMapping("/getStarterBonus")
    public String getStarterPack(@AuthenticationPrincipal Jwt jwt) {
        return logic.getStarterPack(jwt);
    }

}
