package com.malerx.bot.handlers.commands.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.data.model.WeatherMessage;
import com.malerx.bot.handlers.commands.CommandHandler;
import com.malerx.bot.services.weather.WeatherService;
import io.micronaut.core.annotation.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Singleton
@Slf4j
public class WeatherHandler implements CommandHandler {
    private static final String COMMAND = "/weather";

    private final WeatherService weatherService;
    private final ObjectMapper mapper;

    public WeatherHandler(WeatherService weatherService, ObjectMapper mapper) {
        this.weatherService = weatherService;
        this.mapper = mapper;
    }

    @Override
    public Optional<OutgoingMessage> handle(@NonNull Update update) {
        log.debug("handle() -> get weather");
        Optional<String> json = weatherService.getWeather(update);
        if (json.isPresent()) {
            return mapWeather(update.getMessage().getChatId(), json.get());
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private Optional<OutgoingMessage> mapWeather(Long chatId, String jsonWeather) {
        log.debug("mapWeather() -> to answer");
        try {
            Map<String, Object> weather = mapper.readValue(jsonWeather, new TypeReference<>() {
            });
            OutgoingMessage m = new WeatherMessage(Set.of(chatId), ((Map<String, Object>) weather.get("fact")));
            return Optional.of(m);
        } catch (JsonProcessingException e) {
            log.error("mapWeather() -> error parsing: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Boolean support(@NonNull Update update) {
        return update.getMessage().getText().startsWith(COMMAND);
    }
}
