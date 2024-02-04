package com.malerx.bot.handlers;

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

@Singleton
@Slf4j
public class HandlerManager {
    private final Collection<CommandHandler> commands;

    public HandlerManager(Collection<CommandHandler> commands) {
        this.commands = commands;
    }

    public Optional<OutgoingMessage> handle(@NonNull Update update) {
        if (update.hasMessage() && update.getMessage().getText().startsWith("/")) {
            return commandHandling(update);
        } else
            return commandNotDefine(update);
    }

    private Optional<OutgoingMessage> commandHandling(@NonNull Update update) {
        for (CommandHandler handler :
                commands) {
            if (handler.support(update)) {
                return handler.handle(update);
            }
        }
        return commandNotDefine(update);
    }

    private Optional<OutgoingMessage> commandNotDefine(Update update) {
        log.error("commandHandling() -> not found handler for {} command", update.getMessage().getText());
        Set<Long> chatId = Set.of(update.getMessage().getChatId());
        return Optional.of(new TextMessage(chatId, "Команда не определена"));
    }
}
