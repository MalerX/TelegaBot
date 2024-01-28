package com.malerx.bot.handlers.commands.impl;

import com.malerx.bot.data.entity.TGUser;
import com.malerx.bot.data.enums.Role;
import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.data.model.TextMessage;
import com.malerx.bot.data.repository.TGUserRepository;
import com.malerx.bot.handlers.commands.CommandHandler;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.Set;

@Singleton
@Slf4j
public class IpHandler implements CommandHandler {
    private static final String COMMAND = "/ip";
    private final HttpClient client;
    private final String urlIp;
    private final TGUserRepository userRepository;

    public IpHandler(HttpClient client,
                     @Value("${api.yandex.ip}") String urlIp,
                     TGUserRepository userRepository) {
        this.client = client;
        this.urlIp = urlIp;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<OutgoingMessage> handle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var chatId = update.getMessage().getChatId();
            if (isAuthorized(chatId))
                return requestIp(chatId);
            log.warn("handle() -> not authorized user requested host IP");
            return createMsg(chatId, "Запрос не авторизован");
        }
        return Optional.empty();
    }

    private Boolean isAuthorized(Long chatId) {
        log.debug("isAuthorized() -> check role user {}", chatId);
        boolean exist = userRepository.existsById(chatId);
        if (exist) {
            return userRepository.findById(chatId)
                    .map(TGUser::getRole)
                    .map(role -> role.equals(Role.ADMIN))
                    .orElse(false);
        } else
            log.debug("isAuthorized() -> user {} is not registered", chatId);
        return Boolean.FALSE;
    }

    private Optional<OutgoingMessage> requestIp(Long chatId) {
        log.debug("requestIp() -> request hot ip");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlIp))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            log.error("request() -> error send request: ", e);
            return Optional.empty();
        }
        if (StringUtils.isNotEmpty(response.body())) {
            var ip = response.body().substring(1, response.body().length() - 1);
            log.debug("requestIp() -> receive response with IP: {}", ip);
            return createMsg(chatId, ip);
        } else
            return createMsg(chatId, "Ответ на запрос IP не получен");
    }

    private Optional<OutgoingMessage> createMsg(Long chatId, String txt) {
        return Optional.of(new TextMessage(Set.of(chatId), txt));
    }

    @Override
    public Boolean support(Update update) {
        return update.getMessage().getText().startsWith(COMMAND);
    }
}
