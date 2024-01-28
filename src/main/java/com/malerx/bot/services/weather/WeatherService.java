package com.malerx.bot.services.weather;

import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.StringUtils;
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
    private final Position position;

    public WeatherService(HttpClient httpClient, Position position,
                          @Value(value = "${api.yandex.weather}") String weatherToken,
                          @Value(value = "${api.yandex.geo}") String geoToken,
                          @Value(value = "${api.yandex.urlGeo}") String urlGeo) {
        this.httpClient = httpClient;
        this.position = position;
        this.weatherToken = weatherToken;
        this.geoToken = geoToken;
        this.urlGeo = urlGeo;
    }

    public Optional<String> getWeather(@NonNull Update update) {
        try {
            log.debug("handle() -> incoming request weather");
            String[] destination = update.getMessage().getText().split("\\s", 2);
            Optional<Coordinates> coordinates = getCoordinates(destination[1]);
            if (coordinates.isPresent())
                return getWeather(coordinates.get());
            log.error("getCoordinates() -> failed get position for {}", destination[1]);
            return Optional.empty();
        } catch (IOException | InterruptedException e) {
            log.error("getWeather() -> error: ", e);
            return Optional.empty();
        }
    }

    private Optional<Coordinates> getCoordinates(String destination) throws IOException, InterruptedException {
        log.debug("getCoordinates() -> send request pos for {}", destination);
        String uriStr = urlGeo.concat(
                String.format("?format=json&apikey=%s&geocode=%s",
                        geoToken, destination.replace(" ", "+")));
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uriStr))
                .build();
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.debug("getCoordinates() -> receive response with pos");
        if (StringUtils.isNotEmpty(httpResponse.body())) {
            return position.extract(httpResponse.body());
        } else {
            log.error("getCoordinates() -> response body is empty");
        }
        return Optional.empty();
    }

    private Optional<String> getWeather(Coordinates coordinates) throws IOException, InterruptedException {
        if (Objects.isNull(coordinates)) {
            return Optional.empty();
        }
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(coordinates.getUri())
                .header("X-Yandex-API-Key", weatherToken)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return Optional.ofNullable(response.body());
    }
}
