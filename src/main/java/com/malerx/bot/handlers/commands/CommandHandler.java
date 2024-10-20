package com.malerx.bot.handlers.commands;

import com.malerx.bot.data.model.OutgoingMessage;
import io.micronaut.core.annotation.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

/**
 * Сервис обработки входящих событий.
 */
public interface CommandHandler {
    /**
     * Обработка входящего события.
     *
     * @param update Объект события.
     * @return Ответ, созданный по результатам обработки события
     */
    Optional<OutgoingMessage> handle(@NonNull Update update);

    Boolean support(@NonNull Update update);

    String getInfo();
}
