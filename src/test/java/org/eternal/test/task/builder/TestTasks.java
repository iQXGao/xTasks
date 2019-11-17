package org.eternal.test.task.builder;

import java.util.function.Consumer;

import org.eternal.eventbus.EventResult;
import org.eternal.task.Tasks;
import org.eternal.task.builder.Builder.FlowBuilder;
import org.eternal.task.builder.Builder.TaskBuilder;
import org.junit.Test;

public class TestTasks {

    @Test
    public void tsBuildTask() {

        try {

            Tasks tasks = Tasks.generate(FlowBuilder.generate("A")

                    .next("t1", event -> {
                        System.out.println(" handle t1 execute ....");
                        return EventResult.Ok;
                    })

                    .next("t2", event -> {
                        System.out.println(" handle t2 execute ....");
                        return EventResult.Ok;
                    })

                    .next("t3", event -> {
                        System.out.println(" handle t3 execute ....");
                        return EventResult.Error("xxxxxxxxxxxxx----xSF");
                    })

                    .next("t4", event -> {
                        System.out.println(" handle t2 execute ....");
                        return EventResult.Ok;
                    }).gotta(),

                    TaskBuilder.generate("B")

                            .matchEquals("b1", event -> {
                                System.out.println(" task b1 execute ....");
                                return EventResult.Ok;
                            })

                            .matchEquals("b2", event -> {
                                System.out.println(" task b2 execute ....");
                                return EventResult.Ok;
                            }).gotta()

            );

            // -- when B.b1 then B.b2
            tasks.accept("B.b1").then(ra -> {
                try {
                    tasks.accept("B.b2").then(rb -> {
                        System.out.println("-- test call 1 ->>> " + rb.toString());
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // -- when B.b2 then B.b1
            Consumer<EventResult> thenA = result -> {
                System.out.println("-- test call 2 ->>> " + result.toString());
            };

            Consumer<EventResult> thenB = result -> {
                try {
                    tasks.accept("B.b1").then(thenA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            tasks.accept("B.b2").then(thenB);

            // -- loop run B.b1
            for (int i = 0; i < 100; i++) {
                final int n = i;
                tasks.accept("B.b1").then(result -> {
                    System.out.println("--" + "B.b1 " + n + " |||| " + result.toString());
                });
            }

            System.out.println("_------------------------------------------------");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
