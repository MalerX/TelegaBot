package com.malerx.bot.handlers.commands.impl;

import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.services.weather.WeatherMessage;
import com.malerx.bot.handlers.commands.CommandHandler;
import com.malerx.bot.services.weather.WeatherService;
import io.micronaut.core.annotation.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.Set;

@Singleton
@Slf4j
public class WeatherHandler implements CommandHandler {
    private static final String COMMAND = "/погода";

    private final WeatherService weatherService;

    public WeatherHandler(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Override
    public Optional<OutgoingMessage> handle(@NonNull Update update) {
        log.debug("handle() -> get weather");
        String city = update.getMessage().getText().substring(COMMAND.length()).trim();
        return weatherService.getWeather(update)
                .<OutgoingMessage>map(weather -> new WeatherMessage(Set.of(update.getMessage().getChatId()), city, weather))
                .or(Optional::empty);
    }

    @Override
    public Boolean support(@NonNull Update update) {
        return update.getMessage().getText().startsWith(COMMAND);
    }

    @Override
    public String getInfo() {
        return String.format("%s - прогноз погоды в указанном месте", COMMAND);
    }
}
