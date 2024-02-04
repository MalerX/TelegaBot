package com.malerx.bot.services.weather;

import java.net.URI;

public class Coordinates {
    private static final String urlWeather = "https://api.weather.yandex.ru/v2/informers";
    private final String latitude;
    private final String longitude;

    public Coordinates(String pos) {
        String[] coordinates = pos.split("\\s");
        this.longitude = coordinates[0];
        this.latitude = coordinates[1];
    }

    public URI getUri() {
        return URI.create(
                urlWeather.concat(String.format("?lat=%s&lon=%s", latitude, longitude)));
    }
}
