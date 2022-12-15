package com.microservices.limitsservice.presentation;

import com.microservices.limitsservice.business.Configuration;
import com.microservices.limitsservice.business.Limits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LimitsController {

    @Autowired
    private Configuration configuration;

    @GetMapping("/limits")
    public Limits retieveLimits() {
        return new Limits(configuration.getMinimum(), configuration.getMaximum());
    }
}
