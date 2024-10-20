package com.malerx.bot.handlers;

import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.handlers.commands.CommandHandler;
import io.micronaut.core.annotation.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.Optional;

@Singleton
@Slf4j
public class HandlerManager {
    private final Collection<CommandHandler> commands;

    public HandlerManager(Collection<CommandHandler> commands) {
        this.commands = commands;
    }

    public Optional<OutgoingMessage> handle(@NonNull Update update) {
        if (update.hasMessage() && update.getMessage().getText().startsWith("/"))
            return commandHandling(update);
        return Optional.empty();
    }

    private Optional<OutgoingMessage> commandHandling(@NonNull Update update) {
        return commands.stream()
                .filter(handler -> handler.support(update))
                .findAny()
                .flatMap(handler -> handler.handle(update));
    }
}
