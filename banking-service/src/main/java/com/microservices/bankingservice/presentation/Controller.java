package com.microservices.bankingservice.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/banking")
public class Controller {

    @GetMapping("/open")
    public String getOpen() {
        return "U accessed open source";
    }

    @GetMapping("/closed")
    public String getClosed() {
        return "U accessed closed source";
    }
}
