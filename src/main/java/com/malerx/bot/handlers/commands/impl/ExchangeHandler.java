package com.malerx.bot.handlers.commands.impl;

import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.handlers.commands.CommandHandler;
import com.malerx.bot.services.exchange.ExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
@Slf4j
public class ExchangeHandler implements CommandHandler {
    private static final String COMMAND = "/exchange";
    private final ExchangeService exchangeService;

    public ExchangeHandler(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @Override
    public Optional<OutgoingMessage> handle(Update update) {
        log.debug("handle() -> calculate exchange currency by course CBR");
        return exchangeService.exchange(update);
    }

    @Override
    public Boolean support(Update update) {
        return update.getMessage().getText().startsWith(COMMAND);
    }

    @Override
    public String getInfo() {
        return COMMAND + " -- конвертер рублей";
    }
}
