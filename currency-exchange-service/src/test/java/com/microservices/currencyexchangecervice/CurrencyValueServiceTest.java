package com.microservices.currencyexchangecervice;

import com.microservices.currencyexchangecervice.business.Coefficient;
import com.microservices.currencyexchangecervice.business.CurrencyValue;
import com.microservices.currencyexchangecervice.business.CurrencyValueService;
import com.microservices.currencyexchangecervice.persistence.CurrencyValueRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrencyValueServiceTest {
    @InjectMocks
    CurrencyValueService service;
    @Mock
    CurrencyValueRepository repository;


    CurrencyValue val1;
    CurrencyValue val2;
    CurrencyValue val3;
    CurrencyValue val4;

    @BeforeAll
    void init() {
        MockitoAnnotations.initMocks(this);
        val1 = new CurrencyValue(1, "RUB", 1, "1,0");
        val2 = new CurrencyValue(123, "USD", 1, "60,3761");
        val3 = new CurrencyValue(23, "STH", 100, "12,0");
        val4 = new CurrencyValue(44, "AUD", 10, "122,0076");
    }

    @DisplayName("Returning appropriate coefficient")
    @ParameterizedTest
    @MethodSource("provideValuesForCoefficient")
    void testGetCoefficient_whenValuesProvided_shouldReturnAppropriateCoefficient(
            String from, String to, Coefficient expected) throws Exception {

        lenient().when(repository.findByNumCode(1)).thenReturn(val1); //lenient to avoid mockito stubbing
        lenient().when(repository.findByNumCode(123)).thenReturn(val2);
        lenient().when(repository.findByNumCode(23)).thenReturn(val3);
        lenient().when(repository.findByNumCode(44)).thenReturn(val4);

        Coefficient res = service.getCoefficient(from, to);


        assertEquals(expected.getFromCode(), res.getFromCode(), "From is incorrect");
        assertEquals(expected.getToCode(), res.getToCode(), "To is incorrect");
        assertEquals(expected.getCoefficient(), res.getCoefficient(), "Coefficient is incorrect");
    }

    private static Stream<Arguments> provideValuesForCoefficient() {
        return Stream.of(
                Arguments.of("1", "123", new Coefficient("RUB", "USD", 0.0166)),
                Arguments.of("123", "1", new Coefficient("USD", "RUB", 60.3761)),
                Arguments.of("23", "123", new Coefficient("STH", "USD", 0.002)),
                Arguments.of("44", "1", new Coefficient("AUD", "RUB", 12.2008))
        );
    }

}
