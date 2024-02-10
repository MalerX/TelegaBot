package com.malerx.bot.zeebe;

import com.malerx.bot.AssistantBot;
import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.data.model.TextMessage;
import info.novatec.micronaut.zeebe.client.feature.ZeebeWorker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.util.Map;
import java.util.Set;

@Singleton
@ZeebeWorker(type = "send-message")
@Slf4j
public class TgSendWorker implements JobHandler {
    private final AssistantBot bot;

    public TgSendWorker(AssistantBot bot) {
        this.bot = bot;
    }

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        log.debug("handle() -> send message to TG");
        var vars = job.getVariablesAsMap();
        Integer userId = ((Integer) vars.get("userId"));
        String text = vars.get("text").toString();
        OutgoingMessage message = new TextMessage(Set.of(userId.longValue()), text);
        bot.send(message);
        client.newCompleteCommand(job)
                .variables(Map.of("result", "success"))
                .send()
                .whenComplete((completeJobResponse, throwable) -> {
                    if (throwable == null)
                        log.debug("success");
                    else
                        log.error("error: ", throwable);
                });
    }
}
