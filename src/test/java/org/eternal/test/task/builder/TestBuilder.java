package org.eternal.test.task.builder;

import java.util.Optional;

import org.eternal.eventbus.EventResult;
import org.eternal.task.TaskEvent;
import org.eternal.task.builder.Builder.FlowBuilder;
import org.eternal.task.builder.Builder.TaskBuilder;
import org.eternal.task.handler.FlowHandler;
import org.eternal.task.handler.TaskService;
import org.eternal.task.router.Router;
import org.junit.Test;

public class TestBuilder {

    @Test
    public void tsFlowBuilder() {

        try {

            Router router = FlowBuilder.generate("testflow")

                    .next(getFlowHandler("Flow" + "01"))

                    .next(getFlowHandler("Flow" + "02"))

                    .next(getFlowHandler("Flow" + "03"))

                    .next(getFlowHandler("Flow" + "04"))

                    .next(getFlowHandler("Flow" + "05")).gotta().build();

            // --- execute no callback ;
            TaskEvent event = TaskEvent.generate();
            router.accept(event);
            router.accept(event); // 第一次还在执行,次操作自动忽略

            Thread.sleep(5 * 1000);
            System.out.println("------------------------------------");
            router.accept(TaskEvent.generate(), result -> {
                System.out.println("-- Flow run 1 is end , result is " + result.toString());
            });

            router.accept(TaskEvent.generate(), result -> {
                System.out.println("-- Flow run 2 is end , result is " + result.toString());
            });

            System.out.println("------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tsTaskBuilder() {

        try {

            Router router = TaskBuilder.generate("testTask")

                    .match(TestEvent.class, FlowBuilder.generate("x-11")

                            .next(getFlowHandler("Flow" + "06"))

                            .next(getFlowHandler("Flow" + "07"))

                            .next(getFlowHandler("Flow" + "08")))
                    .gotta().build();

            // --
            TaskEvent event = new TestEvent();

            router.accept(event);

            System.out.println("-- 1.......................");
            router.accept(event);

            Thread.sleep(5 * 1000);
            System.out.println("-- 2.......................");

            router.accept(event, result -> {
                System.out.println("-- Task run 1 is end , result is " + result.toString());
            });

            System.out.println("-- 3.......................");

            router.accept(event, result -> {
                System.out.println("-- Task run 2 is end , result is " + result.toString());
            });

            System.out.println("-- 4.......................");

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("------------------------------------");
    }

    // -----------------------------------------------------------
    static public class TestEvent extends TaskEvent.Impl {

    }

    static public FlowHandler getFlowHandler(final String name) {

        return new FlowHandler() {

            TaskService defService = new TaskService.Impl() {

                @Override
                protected EventResult execute(TaskEvent event, EventResult result) {
                    System.out.println("-- flow test service action < " + getFlowHandlerName() + " >" + " Event:{"
                            + event.toString() + " }");
                    return result;
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

        };
    }

}
