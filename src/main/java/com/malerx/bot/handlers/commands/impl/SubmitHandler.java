package com.malerx.bot.handlers.commands.impl;

import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malerx.utils.ResourceUtil;
import io.camunda.zeebe.client.ZeebeClient;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Singleton
@Slf4j
public class SubmitHandler {
    private static final String USER_TASK_COLLECTION = "usertask";
    private static final String SEARCH_USER_TASK = "aql/search_user_task.aql";

    private final ArangoDatabase database;
    private final ZeebeClient client;
    private final ObjectMapper mapper;
//    private final DmnEngine

    public SubmitHandler(ArangoDatabase database, ZeebeClient client, ObjectMapper mapper) {
        this.database = database;
        this.client = client;
        this.mapper = mapper;
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public void submit(Update update) {
        log.debug("submit() -> handle user task");
        SubmitCallback callback = mapper.readValue(update.getCallbackQuery().getData(), SubmitCallback.class);
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        BaseDocument userTask = searchUserTask(callback, chatId);
        if (userTask == null) {
            log.warn("submit() -> user task not found");
            return;
        }
        System.out.println(userTask);
        Collection<String> actions = ((Collection<String>) userTask.getAttribute("actions"));
        if (!actions.contains(callback.action)) {
            log.warn("submit() -> user task not contain action {}", callback.action);
            return;
        }
        long jobKey = ((long) userTask.getAttribute("jobKey"));
        client.newCompleteCommand(jobKey)
                .variables(Map.of("action", callback.action))
                .send()
                .whenComplete((processInstanceEvent, throwable) -> {
                    if (throwable == null)
                        log.debug("handle() -> success submit");
                    else
                        log.error("handle() -> fail submit ", throwable);
                });
    }

    private BaseDocument searchUserTask(SubmitCallback callback, Long user) {
        String aql = ResourceUtil.readFile(SEARCH_USER_TASK);
        Map<String, Object> vars = Map.of("@collection", USER_TASK_COLLECTION,
                "userId", user, "instanceKey", callback.instanceKey);
        try (var cursor = database.query(aql, BaseDocument.class, vars)) {
            List<BaseDocument> userTasks = cursor.asListRemaining();
            if (userTasks.isEmpty())
                return null;
            return userTasks.getFirst();
        } catch (Exception e) {
            log.error("error: ", e);
            return null;
        }
    }

    @Data
    public static class SubmitCallback {
        Long instanceKey;
        String action;
    }
}
