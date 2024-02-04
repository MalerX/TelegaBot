package com.malerx.bot.handlers.commands.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.data.model.TextMessage;
import com.malerx.bot.handlers.commands.CommandHandler;
import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Singleton
@Slf4j
public class ExchangeHandler implements CommandHandler {
    private static final String COMMAND = "/exchange";

    private final HttpClient client;
    private final URI cbr;
    private final HttpResponse.BodyHandler<Exchange> bodyHandler;

    public ExchangeHandler(HttpClient client,
                           @Value("${api.cbr:}") String uri,
                           ObjectMapper mapper) {
        this.client = client;
        this.cbr = URI.create(uri);
        this.bodyHandler = new CustomBodyHandler<>(mapper, Exchange.class);
    }

    @Override
    public Optional<OutgoingMessage> handle(Update update) {
        String[] str = update.getMessage().getText().split(" ");
        if (str.length != 3) {
            log.warn("handle() -> wrong response");
            return message(update, "Неверный формат запроса");
        }
        String currency = str[1];
        Double money = Double.parseDouble(str[2]);
        Map<String, Double> currencies = getExchange().map(Exchange::getRates)
                .orElse(Map.of());
        if (currency.isEmpty())
            return message(update, "Не удалось загрузить курсы валют");
        Double currentCourse = currencies.getOrDefault(currency.toUpperCase(), 0.0);
        Double exchanged = money * currentCourse;
        String message = String.format("Обмен по курсу %f.3 -- %f.3", currentCourse, exchanged);
        return message(update, message);
    }

    @ExecuteOn(TaskExecutors.BLOCKING)
    private Optional<Exchange> getExchange() {
        log.debug("getExchange() -> get exchange course from {}", cbr);
        try {
            HttpRequest request = HttpRequest.newBuilder(cbr).GET().build();
            HttpResponse<Exchange> httpResponse = client.send(request, bodyHandler);
            return Optional.of(httpResponse.body());
        } catch (IOException | InterruptedException e) {
            log.error("handle() -> error sending request: ", e);
            return Optional.empty();
        }
    }

    private Optional<OutgoingMessage> message(Update update, String message) {
        log.debug("message() -> create outgoing message");
        TextMessage error = new TextMessage(Set.of(update.getMessage().getChatId()), message);
        return Optional.of(error);
    }

    @Override
    public Boolean support(Update update) {
        return update.getMessage().getText().startsWith(COMMAND);
    }

    @Override
    public String getInfo() {
        return  "конвертёр валют из рублей в указанную валюту по курсу ЦБР.";
    }
}
