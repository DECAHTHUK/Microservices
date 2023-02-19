package com.microservices.currencyconverisonservice.proxy.handling;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;

public class FeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder errorDecoder = new Default();
    @Override
    public Exception decode(String methodKey, Response response) {
        ExceptionMessage message = null;
        try (InputStream bodyIs = response.body()
                .asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(bodyIs, ExceptionMessage.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }
        switch (message.getStatus()) {
            case 400:
                return new ResponseStatusException(HttpStatus.BAD_REQUEST, message.getMessage());
            case 404:
                return new ResponseStatusException(HttpStatus.NOT_FOUND, message.getMessage());
            default:
                return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Whoops, something went wrong, try again later...");
        }
    }
}
