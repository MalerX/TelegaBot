package com.malerx.bot.handlers.commands.impl;

import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.data.model.TextMessage;
import com.malerx.bot.handlers.commands.CommandHandler;
import io.micronaut.core.annotation.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.Set;

@Singleton
@Slf4j
public class EchoHandler implements CommandHandler {
    private static final String COMMAND = "/echo ";

    @Override
    public Optional<OutgoingMessage> handle(@NonNull Update update) {
        log.debug("handle() -> handle message {}", update.getMessage());
        var id = update.getMessage().getChatId();
        var content = "Echo: ".concat(update.getMessage().getText().substring(COMMAND.length()).trim());

        return Optional.of(new TextMessage(Set.of(id), content));
    }

    @Override
    public Boolean support(@NonNull Update update) {
        return update.getMessage().getText().startsWith(COMMAND);
    }
}
