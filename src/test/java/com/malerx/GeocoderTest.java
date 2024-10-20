package com.malerx;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.malerx.bot.services.weather.GeoData;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@MicronautTest
public class GeocoderTest {
    @Inject
    ObjectMapper mapper;
    HttpClient client = HttpClient.newHttpClient();
    @Inject
    HttpResponse.BodyHandler<GeoData> geoDataBodyHandler;

    @Test
    void requestGeoDataTest() throws IOException, InterruptedException {
        List<String> body = List.of("нижний мамон");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofByteArray(mapper.writeValueAsBytes(body)))
                .uri(URI.create("https://cleaner.dadata.ru/api/v1/clean/address"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Token d91be4a192d88ee2812e9103f7c4ba573e09636f")
                .header("X-Secret", "dd88c868988fe00c806b3e53b772b85960b8711b")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }
}
