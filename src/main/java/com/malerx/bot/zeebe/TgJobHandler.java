package com.malerx.bot.zeebe;

import info.novatec.micronaut.zeebe.client.feature.ZeebeWorker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.util.Map;

@ZeebeWorker(type = "test")
@Singleton
@Slf4j
public class TgJobHandler implements JobHandler {

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        log.debug("handle() -> handle: {} -- {} version: {}",
                job.getBpmnProcessId(), job.getKey(), job.getProcessDefinitionVersion());
        Input vars = job.getVariablesAsType(Input.class);
        Input echo = new Input("echo " + vars.text);
        client.newCompleteCommand(job.getKey())
                .variables(Map.of("result", echo))
                .send()
                .exceptionally(throwable -> {
                    throw new RuntimeException("Could not complete job " + job, throwable);
                });
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Input {
        String text;
    }
}
