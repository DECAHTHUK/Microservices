package com.microservices.limitsservice.presentation;

import com.microservices.limitsservice.business.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class LimitsController {

    @Autowired
    private Configuration configuration;

    @GetMapping("/limits/{thing}/{attribute}")
    public Integer getLimits(@PathVariable String thing, @PathVariable String attribute) {
        switch (thing) {
            case "USD" -> {
                return switch (attribute) {
                    case "standard" -> configuration.getDollarConvDailyMaximum();
                    case "premium" -> configuration.getPremiumDollarConvDailyMaximum();
                    case "max" -> configuration.getDollarInAccountMaximum();
                    default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid attribute: " + attribute + " Expected: standard/premium/max");
                };
            }
            case "EUR" -> {
                return switch (attribute) {
                    case "standard" -> configuration.getEuroConvDailyMaximum();
                    case "premium" -> configuration.getPremiumEuroConvDailyMaximum();
                    case "max" -> configuration.getEuroInAccountMaximum();
                    default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid attribute: " + attribute + " Expected: standard/premium/max");
                };
            }
            case "trans" -> {
                return switch (attribute) {
                    case "min" -> configuration.getMinimumTrans();
                    case "max" -> configuration.getMaximumTrans();
                    case "premium" -> configuration.getPremiumMaximumTrans();
                    case "limit" -> configuration.getDailyTransLimit();
                    default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid attribute: " + attribute + " Expected: min/max/premium/limit");
                };
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid thing: " + attribute + " Expected: USD/EUR/trans");
        }
    }

}
