package io.kestra.core.services;

import com.google.common.collect.ImmutableMap;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import io.kestra.core.models.conditions.Condition;
import io.kestra.core.models.conditions.ConditionContext;
import io.kestra.core.models.conditions.types.ExecutionFlowCondition;
import io.kestra.core.models.conditions.types.ExecutionNamespaceCondition;
import io.kestra.core.models.executions.Execution;
import io.kestra.core.models.flows.Flow;
import io.kestra.core.runners.RunContext;
import io.kestra.core.runners.RunContextFactory;
import io.kestra.core.utils.TestsUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
class ConditionServiceTest {
    @Inject
    ConditionService conditionService;

    @Inject
    RunContextFactory runContextFactory;

    @Test
    void valid() {
        Flow flow = TestsUtils.mockFlow();
        Execution execution = TestsUtils.mockExecution(flow, ImmutableMap.of());

        RunContext runContext = runContextFactory.of(flow, execution);
        ConditionContext conditionContext = conditionService.conditionContext(runContext, flow, execution);

        List<Condition> conditions = Arrays.asList(
            ExecutionFlowCondition.builder()
                .namespace(flow.getNamespace())
                .flowId(flow.getId())
                .build(),
            ExecutionNamespaceCondition.builder()
                .namespace(flow.getNamespace())
                .build()
        );


        boolean valid = conditionService.valid(conditions, conditionContext);

        assertThat(valid, is(true));
    }

    @Test
    void exception() {
        Flow flow = TestsUtils.mockFlow();

        RunContext runContext = runContextFactory.of(flow, (Execution)null);
        ConditionContext conditionContext = conditionService.conditionContext(runContext, flow, null);

        List<Condition> conditions = Collections.singletonList(
            ExecutionFlowCondition.builder()
                .namespace(flow.getNamespace())
                .flowId(flow.getId())
                .build()
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> conditionService.valid(conditions, conditionContext)
        );
    }
}