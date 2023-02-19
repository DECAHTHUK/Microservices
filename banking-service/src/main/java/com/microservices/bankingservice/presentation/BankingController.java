package com.microservices.bankingservice.presentation;

import com.microservices.bankingservice.persistence.service.BankingLogic;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(BankingController.class);

    public BankingController(BankingLogic logic) {
        this.logic = logic;
    }

    @GetMapping("/menu")
    @RateLimiter(name = "high-load", fallbackMethod = "rateFallback")
    @Bulkhead(name = "high-load", fallbackMethod = "bulkFallback")
    public ModelAndView mainMenu(@AuthenticationPrincipal Jwt jwt, Model model) {
        model.addAttribute("username", jwt.getClaims().get("preferred_username").toString());
        return new ModelAndView("main");
    }

    @GetMapping("/get-starter-bonus")
    @RateLimiter(name = "low-load")
    @Bulkhead(name = "low-load")
    public String getStarterPack(@AuthenticationPrincipal Jwt jwt) {
        return logic.getStarterPack(jwt);
    }

    @GetMapping("/get-daily-bonus")
    @RateLimiter(name = "default", fallbackMethod = "rateFallback")
    @Bulkhead(name = "default", fallbackMethod = "bulkFallback")
    public ModelAndView getDailyBonus(@AuthenticationPrincipal Jwt jwt, Model model) {
        return logic.getDailyBonus(jwt, model);
    }

    @GetMapping("/wallet")
    @RateLimiter(name = "high-load", fallbackMethod = "rateFallback")
    @Bulkhead(name = "high-load", fallbackMethod = "bulkFallback")
    public ModelAndView getWallet(@AuthenticationPrincipal Jwt jwt, Model model) {return logic.getWallet(jwt, model);}

    @GetMapping(value = "/convert/{from}/{to}/{quantity}")
    @RateLimiter(name = "default", fallbackMethod = "rateFallback")
    @Bulkhead(name = "default", fallbackMethod = "bulkFallback")
    @Validated
    public ModelAndView convertValutes(@PathVariable String from,
                              @PathVariable String to,
                              @Digits(integer = 10, fraction = 5) @PathVariable double quantity,
                              @AuthenticationPrincipal Jwt jwt, Model model) {
        return logic.convertValutes(from, to, quantity, jwt, model);
    }

    @GetMapping(value = "/transfer/{to}/{code}/{quantity}")
    @RateLimiter(name = "default", fallbackMethod = "rateFallback")
    @Bulkhead(name = "default", fallbackMethod = "bulkFallback")
    @Validated
    public ModelAndView transferValute(@PathVariable String to,
                                   @PathVariable String code,
                                   @Digits(integer = 10, fraction = 5) @PathVariable double quantity,
                                   @AuthenticationPrincipal Jwt jwt, Model model) {
        return logic.transferValutes(to, code, quantity, jwt, model);
    }

    @GetMapping(value = "/transactions")
    @RateLimiter(name = "default", fallbackMethod = "rateFallback")
    @Bulkhead(name = "default", fallbackMethod = "bulkFallback")
    public ModelAndView getTransactions(@AuthenticationPrincipal Jwt jwt, Model model) {
        return logic.findAllTransactions(jwt, model);
    }

    @GetMapping(value = "/get-course")
    @RateLimiter(name = "high-load", fallbackMethod = "rateFallback")
    @Bulkhead(name = "high-load", fallbackMethod = "bulkFallback")
    public ModelAndView getCource(Model model) {
        return logic.getAllCurrencies(model);
    }

    public ModelAndView rateFallback(String from, String to, double quantity, Jwt jwt, Model model, Throwable e) {
        logger.error("Rate limiter has blocked the request, cause - {}", e.toString());
        model.addAttribute("response", e.getMessage());
        return new ModelAndView("error");
    }

    public ModelAndView bulkFallback(String from, String to, double quantity, Jwt jwt, Model model, Throwable e) {
        logger.error("Bulkhead has blocked the request, cause - {}", e.toString());
        model.addAttribute("response", e.getMessage());
        return new ModelAndView("error");
    }

    public ModelAndView rateFallback(Jwt jwt, Model model, Throwable e) {
        logger.error("Rate limiter has blocked the request, cause - {}", e.toString());
        model.addAttribute("response", e.getMessage());
        return new ModelAndView("error");
    }

    public ModelAndView bulkFallback(Jwt jwt, Model model, Throwable e) {
        logger.error("Bulkhead has blocked the request, cause - {}", e.toString());
        model.addAttribute("response", e.getMessage());
        return new ModelAndView("error");
    }

    public String rateFallback(Jwt jwt, Throwable e) {
        logger.error("Rate limiter has blocked the request, cause - {}", e.toString());
        return e.getMessage();
    }

    public String bulkFallback(Jwt jwt, Throwable e) {
        logger.error("Bulkhead has blocked the request, cause - {}", e.toString());
        return e.getMessage();
    }

    public ModelAndView rateFallback(Model model, Throwable e) {
        logger.error("Rate limiter has blocked the request, cause - {}", e.toString());
        model.addAttribute("response", e.getMessage());
        return new ModelAndView("error");
    }

    public ModelAndView bulkFallback(Model model, Throwable e) {
        logger.error("Bulkhead has blocked the request, cause - {}", e.toString());
        model.addAttribute("response", e.getMessage());
        return new ModelAndView("error");
    }
}
