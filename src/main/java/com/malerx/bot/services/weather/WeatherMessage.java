package com.malerx.bot.services.weather;

import com.malerx.bot.data.model.OutgoingMessage;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class WeatherMessage extends OutgoingMessage {
    private final WeatherData weather;

    public WeatherMessage(Set<Long> destination, WeatherData weather) {
        super(destination);
        this.weather = weather;
    }

    @Override
    public Collection<Object> send() {
        String content = this.toString();
        return destination.stream()
                .map(id -> new SendMessage(id.toString(), content))
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        log.debug("buildContent() -> {}", weather.toString());
        Object temp = weather.getFact().getTemp();
        Object humidity = weather.getFact().getHumidity();
        Object windSpeed = weather.getFact().getWindSpeed();
        return new StringJoiner("\n")
                .add("🌡: " + temp)
                .add("🌊: " + humidity)
                .add("🌬 " + windSpeed)
                .toString().trim();
    }
}
