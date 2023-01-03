package com.microservices.currencyexchangecervice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.microservices.currencyexchangecervice.business.CurrencyValue;
import com.microservices.currencyexchangecervice.business.CurrencyValueService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;


@Component
@EnableAsync
public class CurrencyGetter {
    private final CurrencyValueService service;

    public CurrencyGetter(CurrencyValueService repository) {
        this.service = repository;
    }

    @PostConstruct
    public void onStartup() throws IOException, InterruptedException {
        getValues();
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @Async
    public void onSchedule() throws IOException, InterruptedException {
        getValues();
    }

    public void getValues() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://www.cbr.ru/scripts/XML_daily.asp"))
                .timeout(Duration.of(5, SECONDS) )
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        XmlMapper mapper = new XmlMapper();
        List<CurrencyValue> values = mapper.readValue(response.body(), new TypeReference<ArrayList<CurrencyValue>>() {
        });
        values.forEach(t -> service.save(t));
        service.save(new CurrencyValue(-1, 643, "RUB", 1, "Russian Ruble", "1,0"));
    }
}
