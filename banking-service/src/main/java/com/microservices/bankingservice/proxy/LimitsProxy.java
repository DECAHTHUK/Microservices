package com.microservices.bankingservice.proxy;

import com.microservices.bankingservice.proxy.handling.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "limits-service", configuration = FeignConfig.class)
public interface LimitsProxy {
    @GetMapping("/limits/{thing}/{attribute}")
    Integer getLimits(@PathVariable String thing, @PathVariable String attribute);
}
