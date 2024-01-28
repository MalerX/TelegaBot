package com.malerx.bot.data.model;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class WeatherMessage extends OutgoingMessage {
    private final Map<String, Object> weather;

    public WeatherMessage(Set<Long> destination, Map<String, Object> weather) {
        super(destination);
        this.weather = weather;
    }

    @Override
    public Collection<Object> send() {
        String content = buildContent();
        return destination.stream()
                .map(id -> new SendMessage(id.toString(), content))
                .collect(Collectors.toSet());
    }

    private String buildContent() {
        log.debug("buildContent() -> {}", weather.toString());
        Object temp = weather.get("temp");
        Object humidity = weather.get("humidity");
        Object windSpeed = weather.get("wind_speed");
        return new StringJoiner("\n")
                .add("🌡: " + temp)
                .add("🌊: " + humidity)
                .add("🌬 " + windSpeed)
                .toString().trim();
    }

    @Override
    public String toString() {
        return """
                Необходимо использовать преимущество последней версии ЯП.
                """;
    }
}
