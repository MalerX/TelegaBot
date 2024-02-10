package com.malerx.bot;

import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.data.model.TextMessage;
import com.malerx.bot.handlers.HandlerManager;
import com.malerx.bot.handlers.commands.impl.SubmitHandler;
import io.micronaut.context.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.util.Set;
import java.util.concurrent.Executors;

@Singleton
@Slf4j
public class AssistantBot extends TelegramLongPollingBot {

    @Value(value = "${telegram.token}")
    private String token;
    @Value(value = "${telegram.username}")
    private String username;

    private final HandlerManager manager;
    private final SubmitHandler submitHandler;

    public AssistantBot(HandlerManager manager, SubmitHandler submitHandler) {
        this.manager = manager;
        this.submitHandler = submitHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        handle(update);
    }

    private void handle(Update update) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.execute(() -> {
                if (update.hasCallbackQuery())
                    submitHandler.submit(update);
                else {
                    long chatId = update.getMessage().getChatId();
                    log.debug("handle() -> processing message: {}", chatId);
                    manager.handle(update)
                            .ifPresentOrElse(this::send, () -> emptyResponse(chatId));
                }
            });
        }
    }

    private void emptyResponse(Long chatId) {
        log.warn("onUpdateReceived() -> outgoing message of update {} is null", chatId);
        OutgoingMessage errorResponse = new TextMessage(Set.of(chatId), "Невозможно обработать запрос.");
        send(errorResponse);
    }

    public void send(OutgoingMessage outgoing) {
        log.debug("send() -> sending outgoing message");
        outgoing.send().forEach(o -> {
            try {
                if (o instanceof SendMessage message) {
                    log.debug("send() -> object is SendMessage for {} with text {}",
                            message.getChatId(), message.getText());
                    execute(message);
                } else {
                    if (o instanceof SendDocument document) {
                        log.debug("send() -> object is SenDocument for {}", document.getChatId());
                        execute(document);
                    }
                }
            } catch (Exception e) {
                log.error("send() -> sending fail: ", e);
            }
        });
    }

    @Override
    public String getBotUsername() {
        return this.username;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}

