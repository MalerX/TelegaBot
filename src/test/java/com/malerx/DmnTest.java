package com.malerx;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.context.VariableContext;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@MicronautTest
public class DmnTest {
    @Inject
    DmnEngine dmnEngine;

    @Test
    void dmnTest() {
        ClassLoader classLoader = DmnTest.class.getClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream("dmn/DecisionAgeTable.dmn")) {
            List<DmnDecision> dmnDecisions = dmnEngine.parseDecisions(stream);
            VariableContext vars = Variables.createVariables()
                    .putValue("sex", "man")
                    .putValue("hasChild", true)
                    .asVariableContext();
            List<DmnDecisionTableResult> result = dmnDecisions.stream()
                    .map(dd -> dmnEngine.evaluateDecisionTable(dd, vars))
                    .collect(Collectors.toList());
            System.out.println(result);
        } catch (Exception e) {
            Logger.getAnonymousLogger().info(e.getMessage());
        }
    }
}
