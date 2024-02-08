package com.malerx.bot.services.weather;

import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Optional;

@Singleton
@Slf4j
public class WeatherService {
    private final String weatherToken;
    private final String geoToken;
    private final String urlGeo;

    private final HttpClient httpClient;
    private final HttpResponse.BodyHandler<WeatherData> weatherBodyHandler;
    private final HttpResponse.BodyHandler<GeoData> geoDataBodyHandler;
    private final WeatherStorage weatherStorage;

    public WeatherService(HttpClient httpClient,
                          @Value(value = "${api.yandex.weather}") String weatherToken,
                          @Value(value = "${api.yandex.geo}") String geoToken,
                          @Value(value = "${api.yandex.urlGeo}") String urlGeo,
                          HttpResponse.BodyHandler<WeatherData> weatherBodyHandler,
                          HttpResponse.BodyHandler<GeoData> geoDataBodyHandler,
                          WeatherStorage weatherStorage) {
        this.httpClient = httpClient;
        this.weatherToken = weatherToken;
        this.geoToken = geoToken;
        this.urlGeo = urlGeo;
        this.weatherBodyHandler = weatherBodyHandler;
        this.geoDataBodyHandler = geoDataBodyHandler;
        this.weatherStorage = weatherStorage;
    }

    public Optional<WeatherData> getWeather(@NonNull Update update) {
        log.debug("handle() -> incoming request weather");
        String[] destination = update.getMessage().getText().split("\\s", 2);
        return getCoordinates(destination[1])
                .flatMap(this::getWeather)
                .or(Optional::empty);
    }

    private Optional<Coordinates> getCoordinates(String destination) {
        log.debug("getCoordinates() -> send request pos for {}", destination);
        String uriStr = urlGeo.concat(String.format("?format=json&apikey=%s&geocode=%s",
                geoToken, destination.replace(" ", "+")));
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uriStr))
                .build();
        try {
            return Optional.of(httpClient.send(request, geoDataBodyHandler)
                    .body().getCoordinates());
        } catch (IOException | InterruptedException e) {
            log.error("getWeather() -> error: ", e);
            return Optional.empty();
        }
    }

    private Optional<WeatherData> getWeather(Coordinates coordinates) {
        if (Objects.isNull(coordinates)) {
            return Optional.empty();
        }
        Optional<WeatherData> cached = weatherStorage.searchWeather(coordinates.getCity());
        if (cached.isPresent())
            return cached;
        HttpRequest request = coordinates.request(weatherToken);
        try {
            WeatherData weather = httpClient.send(request, weatherBodyHandler).body();
            weatherStorage.saveWeather(coordinates.getCity(), weather);
            return Optional.of(weather);
        } catch (InterruptedException | IOException e) {
            return Optional.empty();
        }
    }
}
