package com.malerx.bot.services.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malerx.bot.storage.AbstractCache;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Singleton
@Slf4j
public class WeatherService {
    private final String weatherToken;
    private final String geoToken;
    private final URI urlGeo;
    private final String geoSecret;

    private final HttpClient httpClient;
    private final HttpResponse.BodyHandler<WeatherData> weatherBodyHandler;
    private final HttpResponse.BodyHandler<GeoData> geoDataBodyHandler;
    private final AbstractCache<WeatherData> cache;
    private final ObjectMapper mapper;

    public WeatherService(HttpClient httpClient,
                          @Value(value = "${api.yandex.weather}") String weatherToken,
                          @Value(value = "${api.geoToken}") String geoToken,
                          @Value(value = "${api.geoUrl}") String urlGeo,
                          @Value(value = "${api.geoSecret}") String geoSecret,
                          HttpResponse.BodyHandler<WeatherData> weatherBodyHandler,
                          HttpResponse.BodyHandler<GeoData> geoDataBodyHandler,
                          AbstractCache<WeatherData> cache,
                          ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.weatherToken = weatherToken;
        this.geoToken = "Token " + geoToken;
        this.urlGeo = URI.create(urlGeo);
        this.geoSecret = geoSecret;
        this.weatherBodyHandler = weatherBodyHandler;
        this.geoDataBodyHandler = geoDataBodyHandler;
        this.cache = cache;
        this.mapper = mapper;
    }

    public Optional<WeatherData> getWeather(@NonNull Update update) {
        log.debug("handle() -> incoming request weather");
        String[] destination = update.getMessage().getText().split("\\s", 2);
        try {
            return getCoordinates(destination[1])
                    .flatMap(this::getWeather)
                    .or(Optional::empty);
        } catch (JsonProcessingException e) {
            log.debug("getWeather() -> oops...", e);
            return Optional.empty();
        }
    }

    private Optional<Coordinates> getCoordinates(String destination) throws JsonProcessingException {
        log.debug("getCoordinates() -> send request pos for {}", destination);
        byte[] body = mapper.writeValueAsBytes(List.of(destination));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .uri(urlGeo)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", geoToken)
                .header("X-Secret", geoSecret)
                .build();
        try {
            HttpResponse<GeoData> data = httpClient.send(request, geoDataBodyHandler);
            if (data.statusCode() == 200) {
                GeoData geoData = data.body();
                return Optional.of(new Coordinates(geoData.getSettlement(), geoData.getGeoLat(), geoData.getGeoLon()));
            } else {
                log.error("getCoordinates() -> fail get coordinates, code: {}", data.statusCode());
                return Optional.empty();
            }
        } catch (IOException | InterruptedException e) {
            log.error("getWeather() -> error: ", e);
            return Optional.empty();
        }
    }

    private Optional<WeatherData> getWeather(Coordinates coordinates) {
        if (Objects.isNull(coordinates)) {
            return Optional.empty();
        }
        Optional<WeatherData> cached = cache.searchDocument(coordinates.getCity());
        if (cached.isPresent())
            return cached;
        HttpRequest request = coordinates.request(weatherToken);
        try {
            WeatherData weather = httpClient.send(request, weatherBodyHandler).body();
            cache.saveDocument(weather, coordinates.getCity());
            return Optional.of(weather);
        } catch (InterruptedException | IOException e) {
            return Optional.empty();
        }
    }
}
