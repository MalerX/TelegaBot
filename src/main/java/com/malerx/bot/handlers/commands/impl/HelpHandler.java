package com.malerx.bot.handlers.commands.impl;

import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.data.model.TextMessage;
import com.malerx.bot.handlers.commands.CommandHandler;
import io.micronaut.core.annotation.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

//@Singleton
@Slf4j
public class HelpHandler implements CommandHandler {
    private static final String COMMAND = "/help";
    private static final String header = "Добро пожаловать в интерактивную мультимедийную систему бота Технопарка.\n\n";

    private final Collection<CommandHandler> handlers;

    public HelpHandler(Collection<CommandHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public Optional<OutgoingMessage> handle(@NonNull Update update) {
        log.debug("handle() -> get info");
        var message = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage() :
                (update.hasMessage() ? update.getMessage() : null);
        if (message != null) {
            return Optional.of(createMessage(message.getChatId()));
        }
        return Optional.empty();
    }

    private OutgoingMessage createMessage(long chatId) {
        String allHandlers = handlers.stream()
                .map(CommandHandler::getInfo)
                .collect(Collectors.joining("\n", header, "."));
        return new TextMessage(
                Set.of(chatId), allHandlers);
    }

    @Override
    public Boolean support(@NonNull Update update) {
        return update.getMessage().getText().startsWith(COMMAND);
    }

    @Override
    public String getInfo() {
        return  "информация о возможностей хэндлера";
    }
}
