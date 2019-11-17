package org.eternal.test.task.samples.s03;

import java.util.concurrent.atomic.AtomicInteger;

import org.eternal.eventbus.EventResult;
import org.eternal.task.Tasks;
import org.eternal.test.task.samples.TasksConfig;
import org.junit.Before;
import org.junit.Test;

public class TestS03 {

    static String taskcode = "test-task-03";
    static Tasks tasks;

    static String flownameA = "bs-flow-01";

    static String flownameB = "bs-flow-02";

    static String flownameC = "bs-flow-03";

    static String flownameD = "bs-flow-04";
    static String flownameE = "bs-flow-05";

    @Before
    public void init() {
        TasksConfig cfg = new TasksConfig(taskcode);
        cfg.addFlow(flownameA, flow -> {
            flow.next(new TestSHandler("fa1")).next(new TestSHandler("fa2")).next(new TestSHandler("fa3"));
        });

        cfg.addFlow(flownameB, flow -> {
            flow.next("fb1", event -> {
                System.out.println("-- " + "fb1  ok ...");
                return EventResult.Ok;
            }).next("fb2", event -> {
                try {
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                System.out.println("-- " + "fb2  ok ...");
                return EventResult.Ok;
            }).next("fb3", event -> {

                System.out.println("-- " + "fb3  ok ...");
                return EventResult.Ok;
            });
        });

        cfg.addFlow(flownameC, flow -> {
            flow.next("fc1", event -> {
                try {
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                System.out.println("-- " + "fc1  ok ...");
                return EventResult.Ok;
            }).next("fc2", event -> {

                System.out.println("-- " + "fc2  ok ...");
                return EventResult.Ok;
            }).next("fc3", event -> {

                System.out.println("-- " + "fc3  ok ...");
                return EventResult.Ok;
            }).next("fc4", event -> {

                System.out.println("-- " + "fc4  ok ...");
                return EventResult.Ok;
            });
        });

        cfg.addFlow(flownameD, flow -> {
            flow.next("fd1", event -> {
                try {
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                System.out.println("-- " + "fd1  ok ...");
                return EventResult.Ok;
            }).next("fd2", event -> {

                System.out.println("-- " + "fd2  ok ...");
                return EventResult.Ok;
            });
        });

        cfg.addFlow(flownameE, flow -> {
            flow.next("fe1", event -> {
                try {
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                System.out.println("-- " + "fe1  ok ...");
                return EventResult.Ok;
            }).next("fe2", event -> {

                System.out.println("-- " + "fe2  ok ...");
                return EventResult.Ok;
            });
        });

        cfg.addTask(task -> {
            task.matchEquals("AAA", event -> {

                final AtomicInteger n = new AtomicInteger(0);

                try {
                    tasks.accept(flownameA).then(result -> {
                        n.incrementAndGet();

                        System.out.println("-- " + flownameA + "  ok ...");
                        if (n.get() >= 3) {
                            try {
                                tasks.accept(taskcode + ".BBB").then(r -> {
                                    System.out.println("--" + flownameB + " .BBB FFFFF");
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    tasks.accept(flownameB).then(result -> {
                        n.incrementAndGet();

                        System.out.println("-- " + flownameB + "  ok ...");
                        if (n.get() >= 3) {
                            try {
                                tasks.accept(taskcode + ".BBB").then(r -> {
                                    System.out.println("--" + flownameB + " .BBB FFFFF");
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    tasks.accept(flownameC).then(result -> {
                        n.incrementAndGet();

                        System.out.println("-- " + flownameC + "  ok ...");
                        if (n.get() >= 3) {
                            try {
                                tasks.accept(taskcode + ".BBB").then(r -> {
                                    System.out.println("--" + flownameB + " .BBB FFFFF");
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return EventResult.Ok;
            }).matchEquals("BBB", event -> {

                try {
                    tasks.accept(flownameD).then(result -> {

                        System.out.println("-- " + flownameD + "  ok ...");
                        try {
                            tasks.accept(flownameE).then(end -> {
                                System.out.println("-- end ........");
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return EventResult.Ok;
            });
        });

        tasks = cfg.getTasks();
    }

    @Test
    public void tsXX() {
        for (int i = 0; i < 100; i++) {
            try {
                final int n = i;
                tasks.accept(taskcode + ".AAA").then(result -> {
                    System.out.println("-- " + n
                            + "--------xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx-----");
                });
                //Thread.sleep(1 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // -------------------------------------------------------------
            System.out.println("-----------------------------------");

        }
        System.out.println("-----------------------------------");
    }
}
