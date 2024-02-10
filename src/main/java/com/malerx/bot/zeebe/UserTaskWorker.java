package com.malerx.bot.zeebe;

import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import info.novatec.micronaut.zeebe.client.feature.ZeebeWorker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.util.Map;

@ZeebeWorker(type = "io.camunda.zeebe:userTask")
@Singleton
@Slf4j
public class UserTaskWorker implements JobHandler {
    private static final String USER_TASK_COLLECTION = "usertask";
    private final ArangoDatabase database;

    public UserTaskWorker(ArangoDatabase database) {
        this.database = database;
    }

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        log.debug("handle() -> handle: {} -- {} version: {}",
                job.getBpmnProcessId(), job.getKey(), job.getProcessDefinitionVersion());
        Map<String, Object> vars = job.getVariablesAsMap();
        BaseDocument userTask = new BaseDocument();
        userTask.addAttribute("actions", vars.get("actions"));
        userTask.addAttribute("userId", vars.get("userId"));
        userTask.addAttribute("processId", job.getBpmnProcessId());
        userTask.addAttribute("instanceKey", job.getProcessInstanceKey());
        userTask.addAttribute("jobKey", job.getKey());
        userTask.addAttribute("date", LocalDateTime.now().toString());
        database.collection(USER_TASK_COLLECTION).insertDocument(userTask);
    }
}
