package com.malerx.bot.handlers.commands.impl;

import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.data.model.TextMessage;
import com.malerx.bot.handlers.commands.CommandHandler;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.friends.responses.GetResponse;
import com.vk.api.sdk.objects.messages.responses.GetHistoryResponse;
import com.vk.api.sdk.objects.newsfeed.responses.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.Set;

//@Singleton
@Slf4j
public class VkHandler implements CommandHandler {
    private static final Long OWNER = 421448493L;
    private static final String COMMAND = "/vk";
    private final VkApiClient vkClient;
    private final UserActor actor;

    public VkHandler(VkApiClient vkClient, UserActor actor) {
        this.vkClient = vkClient;
        this.actor = actor;
    }

    @Override
    public Optional<OutgoingMessage> handle(Update update) {
        if (!update.getMessage().getChatId().equals(OWNER))
            return Optional.empty();
        GetHistoryResponse response;
        try {
            response = vkClient.messages().getHistory(actor)
                    .execute();
        } catch (ApiException | ClientException e) {
            log.error("handle() -> error: ", e);
            return Optional.empty();
        }
        Set<Long> chatId = Set.of(update.getMessage().getChatId());
        TextMessage message = new TextMessage(chatId, response.toString());
        return Optional.of(message);
    }

    @Override
    public Boolean support(Update update) {
        return update.getMessage().getText().startsWith(COMMAND);
    }

    @Override
    public String getInfo() {
        return "VK";
    }
}
