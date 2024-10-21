package com.malerx.bot.handlers.commands.impl;

import com.arangodb.ArangoDatabase;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;

@Singleton
@Slf4j
public class SubmitHandler {
    private final ArangoDatabase database;
    private final ZeebeClient client;
    private final ObjectMapper mapper;

    public SubmitHandler(ArangoDatabase database, ZeebeClient client, ObjectMapper mapper) {
        this.database = database;
        this.client = client;
        this.mapper = mapper;
    }

    @SneakyThrows
    public void submit(Update update) {
        log.debug("submit() -> ");
        SubmitCallback callback = mapper.readValue(update.getCallbackQuery().getData(), SubmitCallback.class);
        System.out.println(callback);
    }

    @Data
    public static class SubmitCallback {
        String instanceKey;
        String action;
    }
}
