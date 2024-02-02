package com.malerx.bot;

import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.handlers.HandlerManager;
import io.micronaut.context.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.util.concurrent.StructuredTaskScope;

@Singleton
@Slf4j
public class AssistantBot extends TelegramLongPollingBot {
    @Value(value = "${telegram.token}")
    private String token;
    @Value(value = "${telegram.username}")
    private String username;

    private final HandlerManager manager;

    public AssistantBot(HandlerManager manager) {
        this.manager = manager;
    }

    @Override
    public void onUpdateReceived(Update update) {
        handle(update);
    }

    private void handle(Update update) {

        try (var scope = new StructuredTaskScope<Void>()) {
            scope.fork(() -> {
                long chatId = update.getMessage().getChatId();
                log.debug("handle() -> processing message: {}", chatId);
                manager.handle(update)
                        .ifPresentOrElse(this::send,
                                () -> log.warn("onUpdateReceived() -> outgoing message of update {} is null", chatId));
                return null;
            });
            scope.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void send(OutgoingMessage outgoing) {
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

