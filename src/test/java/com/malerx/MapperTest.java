package com.malerx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malerx.bot.handlers.commands.impl.Exchange;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@MicronautTest
public class MapperTest {
    private static final String json = """
            {
                "disclaimer": "https://www.cbr-xml-daily.ru/#terms",
                "date": "2024-01-27",
                "timestamp": 1706302800,
                "base": "RUB",
                "rates": {
                    "AUD": 0.0169904,
                    "AZN": 0.01899104,
                    "GBP": 0.0087962197,
                    "AMD": 4.50881,
                    "BYN": 0.0355725,
                    "BGN": 0.020057887,
                    "BRL": 0.0549946,
                    "HUF": 3.9778355,
                    "VND": 268.511159,
                    "HKD": 0.087135,
                    "GEL": 0.0299042,
                    "DKK": 0.07646487586,
                    "AED": 0.0410263,
                    "USD": 0.01117119975,
                    "EUR": 0.01029924,
                    "EGP": 0.3451108,
                    "INR": 0.928703,
                    "IDR": 176.1363436,
                    "KZT": 4.982685,
                    "CAD": 0.015076657,
                    "QAR": 0.040663,
                    "KGS": 0.9978147856,
                    "CNY": 0.080402,
                    "MDL": 0.1981469,
                    "NZD": 0.01829544956,
                    "NOK": 0.1168138,
                    "PLN": 0.0451239,
                    "RON": 0.051177596,
                    "XDR": 0.00838175,
                    "SGD": 0.0149660495,
                    "TJS": 0.122338,
                    "THB": 0.39856675,
                    "TRY": 0.337882,
                    "TMT": 0.039099,
                    "UZS": 138.6570235,
                    "UAH": 0.4198417,
                    "CZK": 0.253921,
                    "SEK": 0.1163983,
                    "CHF": 0.0096630868,
                    "RSD": 1.207063,
                    "ZAR": 0.21131,
                    "KRW": 14.928069,
                    "JPY": 1.64886986
                }
            }""";

    @Inject
    ObjectMapper mapper;

    @Test
    void mapperTest() throws IOException {
        Exchange exchange = mapper.readValue(json.getBytes(StandardCharsets.UTF_8), Exchange.class);
        Logger.getAnonymousLogger().info(exchange.toString());
    }
}
