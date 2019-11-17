package org.eternal.task.builder;

import java.util.Optional;
import java.util.function.Function;

import org.eternal.eventbus.EventResult;
import org.eternal.task.TaskEvent;
import org.eternal.task.builder.BuilderImpl;
import org.eternal.task.handler.FlowHandler;
import org.eternal.task.handler.TaskHandler;
import org.eternal.task.handler.TaskService;
import org.eternal.task.router.Router;

/**
 * 
 * @author gaoqi
 *
 */

public interface Builder {

    Router build() throws Exception;

    String getName();

    public interface FlowBuilder {

        Builder gotta();

        default FlowBuilder next(String name, Function<TaskEvent, EventResult> func) {
            return next(name, new FlowHandler() {

                final TaskService defService = new TaskService.Impl() {
                    @Override
                    protected EventResult execute(TaskEvent event, EventResult result) {
                        return result.isSuccessful() ? func.apply(event) : result;
                    }
                };

                @Override
                public String getFlowHandlerName() {
                    return name;
                }

                @SuppressWarnings("unchecked")
                @Override
                protected <V, S extends TaskService> S getTaskService(Optional<V> args) {
                    return (S) defService;
                }

            });
        }

        default FlowBuilder next(FlowHandler handler) {
            return next(handler.getFlowHandlerName(), handler);
        }

        FlowBuilder next(String name, TaskHandler handler);

        static FlowBuilder generate(String name) {
            return new BuilderImpl.FlowBuilderImpl(name);
        }
    }

    public interface TaskBuilder {

        TaskBuilder match(Class<? extends TaskEvent> type, FlowBuilder flow);

        TaskBuilder matchFlow(String value, FlowBuilder flow);

        <T extends TaskHandler> TaskBuilder match(Class<? extends TaskEvent> type, T handler);

        TaskBuilder matchEquals(String value, TaskHandler handler);

        TaskBuilder match(Class<? extends TaskEvent> type, Function<TaskEvent, EventResult> func);

        TaskBuilder matchEquals(String value, Function<TaskEvent, EventResult> func);

        static TaskBuilder generate(String code) {
            return new BuilderImpl.TaskBuilderImpl(code);
        }

        Builder gotta();
    }
}
