package com.microservices.currencyexchangecervice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.microservices.currencyexchangecervice.business.CurrencyValue;
import com.microservices.currencyexchangecervice.business.CurrencyValueService;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

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
        getCurrencyValues();
        getCryptoValues();
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @Async
    public void onScheduleNational() throws IOException, InterruptedException {
        getCurrencyValues();
    }

    @Scheduled(initialDelay = 3600000, fixedDelay = 3600000)
    public void onScheduleCrypto() throws IOException, InterruptedException {
        getCryptoValues();
    }

    public void getCurrencyValues() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://www.cbr.ru/scripts/XML_daily.asp"))
                .timeout(Duration.of(5, SECONDS) )
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        XmlMapper mapper = new XmlMapper();
        List<CurrencyValue> values = mapper.readValue(response.body(), new TypeReference<ArrayList<CurrencyValue>>() {
        });
        values.remove(0);
        values.forEach(service::save);
        service.save(new CurrencyValue( 643, "RUB", 1, "Russian Ruble", "1,0"));
    }

    public void getCryptoValues() throws IOException, InterruptedException {
        Properties login = new Properties();
        ClassLoader classLoader = getClass().getClassLoader();
        try (FileReader in = new FileReader(classLoader.getResource("login.properties").getFile())) {
            login.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://fcsapi.com/api-v3/crypto/supply?sort=rank&order=ASC&limit=99&access_key=" + login.getProperty("API_KEY")))
                .timeout(Duration.of(5, SECONDS) )
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        JSONObject jsonObject = new JSONObject(response.body());
        JSONArray jsonArray = jsonObject.getJSONArray("response");

        double coefficient = service.getCoefficient("USD", "RUB").getCoefficient();

        for (int i = 0; i < jsonArray.length(); i++) {
            CurrencyValue value = new CurrencyValue();
            JSONObject current = jsonArray.getJSONObject(i);
            value.setCharCode(current.getString("symbol"));
            value.setCurrencyName(current.getString("name"));
            value.setNominal(1);
            value.setPower(current.getJSONObject("quote").getJSONObject("USD").getString("price"));
            service.saveCrypto(value, coefficient);
        }

    }
}
