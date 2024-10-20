package com.malerx.bot.zeebe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malerx.bot.AssistantBot;
import com.malerx.bot.data.model.ButtonMessage;
import info.novatec.micronaut.zeebe.client.feature.ZeebeWorker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
@ZeebeWorker(type = "echo-worker")
@Slf4j
public class EchoWorker implements JobHandler {
    private final AssistantBot bot;
    private final ObjectMapper mapper;

    public EchoWorker(AssistantBot bot, ObjectMapper mapper) {
        this.bot = bot;
        this.mapper = mapper;
    }

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        log.debug("handle() -> echo for {}--{}, version: {}",
                job.getBpmnProcessId(), job.getKey(), job.getProcessDefinitionVersion());
        Input input = job.getVariablesAsType(Input.class);
        buildMessage(input, job.getProcessInstanceKey());
        String echo = "echo -- " + input.text;
        Map<String, Object> vars = Map.of("actions", List.of("yes", "no"),
                "echo", echo);
        client.newCompleteCommand(job)
                .variables(vars)
                .send()
                .whenComplete((processInstanceEvent, throwable) -> {
                    if (throwable == null)
                        log.debug("handle() -> success complete");
                    else
                        log.error("handle() -> fail complete ", throwable);
                });
    }

    @SneakyThrows
    private void buildMessage(Input input, long instanceKey) {
        log.debug("buildMessage() -> build message");
        InlineKeyboardButton yes = new InlineKeyboardButton();
        yes.setText("ДА");
        String callbackYes = mapper.writeValueAsString(Map.of("instanceKey", instanceKey, "action", "yes"));
        yes.setCallbackData(callbackYes);
        InlineKeyboardButton no = new InlineKeyboardButton();
        no.setText("НЕТ");
        String callbackNo = mapper.writeValueAsString(Map.of("instanceKey", instanceKey, "action", "no"));
        no.setCallbackData(callbackNo);
        ReplyKeyboard keyboard = new InlineKeyboardMarkup(List.of(List.of(yes, no)));
        bot.send(new ButtonMessage("echo: " + input.text, Set.of(input.userId), keyboard));
    }

    @Data
    public static class Input {
        Long userId;
        String text;
    }
}
