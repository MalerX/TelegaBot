package com.malerx.bot.zeebe;

import info.novatec.micronaut.zeebe.client.feature.ZeebeWorker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;

@Singleton
@ZeebeWorker(type = "send-message")
@Slf4j
public class TgSendWorker implements JobHandler {
    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        log.debug("handle() -> send message to TG");
        var vars = job.getVariablesAsMap();
        client.newCompleteCommand(job)
                .send()
                .whenComplete((completeJobResponse, throwable) -> {
                    if (throwable == null)
                        log.debug("success");
                    else
                        log.error("error: ", throwable);
                });
    }
}
