package com.microservices.apigateway;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Component
public class MyErrorViewResolver implements ErrorViewResolver {

    Logger logger = LoggerFactory.getLogger(MyErrorViewResolver.class);

    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        if (status == HttpStatus.NOT_FOUND) {
            logger.debug("Not Found: " + request.getPathInfo());
            return new ModelAndView("404");
        } else if (status == HttpStatus.SERVICE_UNAVAILABLE) {
            logger.error("Service unavailabe: " + request.getPathInfo());
            return new ModelAndView("503");
        } else {
            logger.error("SOMETHING WENT WRONG " + status + " Caused by:" + request.getPathInfo());
            return new ModelAndView("500");
        }
    }
}

