package com.malerx.bot.services.weather;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpRequest;

@Slf4j
public class Coordinates {
    private static final String urlWeather = "https://api.weather.yandex.ru/v2/informers";
    private static final String API_KEY = "X-Yandex-API-Key";
    @Getter
    private final String city;
    private final String latitude;
    private final String longitude;

    public Coordinates(String city, String latitude, String longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.city = city;
    }

    public HttpRequest request(String weatherToken) {
        URI uri = URI.create(urlWeather.concat(String.format("?lat=%s&lon=%s", latitude, longitude)));
        return HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header(API_KEY, weatherToken)
                .build();
    }
}
