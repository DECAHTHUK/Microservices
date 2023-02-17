package com.microservices.bankingservice.presentation;

import com.microservices.bankingservice.persistence.service.BankingLogic;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.constraints.Digits;

@RestController
@RequestMapping("/banking")
public class BankingController {
    private final BankingLogic logic;

    public BankingController(BankingLogic logic) {
        this.logic = logic;
    }

    @GetMapping("/menu")
    public ModelAndView mainMenu(Model model, @AuthenticationPrincipal Jwt jwt) {
        model.addAttribute("username", jwt.getClaims().get("preferred_username").toString());
        return new ModelAndView("main");
    }

    @GetMapping("/get-starter-bonus")
    public String getStarterPack(@AuthenticationPrincipal Jwt jwt) {
        return logic.getStarterPack(jwt);
    }

    @GetMapping("/get-daily-bonus")
    public ModelAndView getDailyBonus(@AuthenticationPrincipal Jwt jwt, Model model) {
        return logic.getDailyBonus(jwt, model);
    }

    @GetMapping("/wallet")
    public ModelAndView getWallet(@AuthenticationPrincipal Jwt jwt, Model model) {return logic.getWallet(jwt, model);}

    @GetMapping(value = "/convert/{from}/{to}/{quantity}")
    @Validated
    public ModelAndView convertValutes(@PathVariable String from,
                              @PathVariable String to,
                              @Digits(integer = 10, fraction = 5) @PathVariable double quantity,
                              @AuthenticationPrincipal Jwt jwt, Model model) {
        return logic.convertValutes(from, to, quantity, jwt, model);
    }

    @GetMapping(value = "/transfer/{to}/{code}/{quantity}")
    @Validated
    public ModelAndView transferValute(@PathVariable String to,
                                   @PathVariable String code,
                                   @Digits(integer = 10, fraction = 5) @PathVariable double quantity,
                                   @AuthenticationPrincipal Jwt jwt, Model model) {
        return logic.transferValutes(to, code, quantity, jwt, model);
    }

    @GetMapping(value = "/transactions")
    public ModelAndView getTransactions(@AuthenticationPrincipal Jwt jwt, Model model) {
        return logic.findAllTransactions(jwt, model);
    }

    @GetMapping(value = "/get-course")
    public ModelAndView getCource(Model model) {
        return logic.getAllCurrencies(model);
    }

}
