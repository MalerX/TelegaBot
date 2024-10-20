package com.malerx.bot.handlers.commands.impl;

import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.data.model.TextMessage;
import com.malerx.bot.handlers.commands.CommandHandler;
import io.camunda.zeebe.client.ZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Singleton
@Slf4j
public class TestZeebeHandler implements CommandHandler {
    private static final String COMMAND = "/zeebe";
    private static final String TEST_PROCESS = "telega-process";

    private final ZeebeClient zeebeClient;

    public TestZeebeHandler(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @Override
    public Optional<OutgoingMessage> handle(Update update) {
        log.debug("handle() -> start test zeebe process");
        Long userId = update.getMessage().getChatId();
        String text = update.getMessage().getText().split(" ")[1];
        Map<String, Object> vars = Map.of("userId", userId, "text", text);
        zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(TEST_PROCESS)
                .latestVersion()
                .variables(vars)
                .send().join();
//                .whenComplete((processInstanceEvent, throwable) -> {
//                    if (throwable == null)
//                        log.debug("handle() -> success start");
//                    else
//                        log.error("handle() -> fail start ", throwable);
//                });
        OutgoingMessage message = new TextMessage(Set.of(userId), "start BP");
        return Optional.of(message);
    }

    @Override
    public Boolean support(Update update) {
        return update.getMessage().getText().startsWith(COMMAND);
    }

    @Override
    public String getInfo() {
        return "test start BP";
    }
}
