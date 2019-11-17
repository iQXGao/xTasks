package org.eternal.test.task.samples.s01;

import java.util.function.Consumer;

import org.eternal.eventbus.EventResult;
import org.eternal.task.Tasks;
import org.eternal.test.task.samples.TasksConfig;
import org.junit.Before;
import org.junit.Test;

public class TestS01 {

	static final String taskcode = "S01-TEST";

	static final String flow1 = "flow01";
	static final String flow2 = "flow02";

	static Tasks tasks;

	@Before
	public void init() {
		TasksConfig cfg = new TasksConfig(taskcode);

		cfg.addFlow(flow1, flow -> {
			flow.next("f1", event -> {
				System.out.println("-- f1 executor ...");
				return EventResult.Ok;
			}).next("f2", event -> {
				System.out.println("-- f2 executor ...");
				return EventResult.Ok;
			}).next("f3", event -> {
				System.out.println("-- f3 executor ...");
				return EventResult.Ok;
			});
		});

		cfg.addFlow(flow2, flow -> {
			flow.next("f1", event -> {
				System.out.println("-- f4 executor ...");
				return EventResult.Ok;
			}).next("f2", event -> {
				System.out.println("-- f5 executor ...");
				return EventResult.Ok;
			}).next("f3", event -> {
				System.out.println("-- f6 executor ...");
				return EventResult.Ok;
			});
		});

		cfg.addTask(task -> {
			task.matchEquals("t1", event -> {
				System.out.println("-- t1 executor ...");
				return EventResult.Ok;
			}).matchEquals("t2", event -> {
				System.out.println("-- t2 executor ...");
				return EventResult.Ok;
			}).matchEquals("t3", event -> {
				System.out.println("-- t3 executor ...");
				try {
					Thread.sleep(1 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return EventResult.Ok;
			}).matchEquals("t4", event -> {
				System.out.println("-- t4 executor ...");
				return EventResult.Error("test error t4");
			})

			;
		});

		tasks = cfg.getTasks();
	}

	/***
	 * when flow1 then flow2
	 * 
	 * @throws Exception
	 */
	static public void flow1to2() throws Exception {
		EventResult result = tasks.accept(flow1).then(flow1Result -> {
			if (flow1Result.isSuccessful()) {
				try {
					tasks.accept(flow2).then(flow2Result -> {
						System.out.println("-- flow1to2 execute finished ....");
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		System.out.println("-- running flow1to2  result :" + result.toString());
	}

	/**
	 * task.node graph execute graph{n1->n3->n2-n4}
	 * 
	 * @throws Exception
	 */
	static public void taskgraph() throws Exception {
		System.out.println("\n" + "-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- ");
		Consumer<EventResult> then4 = result -> {
			try {
				tasks.accept(taskcode + ".t4").then(end -> {
					if (end.isSuccessful()) {
						System.out.println("-- t4 ............");
					} else {
						System.out.println("-- t4 xxxxxxxxxxxx");
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		Consumer<EventResult> then3 = result -> {
			try {
				tasks.accept(taskcode + ".t2").then(then4);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		Consumer<EventResult> then2 = result -> {
			try {
				tasks.accept(taskcode + ".t3").then(then3);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		tasks.accept(taskcode + ".t1").then(then2);
	}

 @Test
	public void testFlow() throws Exception {
		flow1to2();
		System.out.println("-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- ");
		for (int i = 1; i < 100; i++) {
			flow1to2();
			System.out.println("-- " + i + " -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- ");
			Thread.sleep(3 * 1000);
		}
	}

//	@Test
	public void testTask() throws Exception {
		// simple node execute and then ...
		
		System.out.println("\n" + "-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- ");
		tasks.accept(taskcode + ".t1").then(result -> {
			System.out.println("-- t1 " + result.toString());
		});

		tasks.accept(taskcode + ".t2").then(result -> {
			System.out.println("-- t2 " + result.toString());
		});
		tasks.accept(taskcode + ".t3").then(result -> {
			System.out.println("-- t3 " + result.toString());
		});
		tasks.accept(taskcode + ".t4").then(result -> {
			System.out.println("-- t4 " + result.toString());
		});
	}

	//@Test
	public void testTaskThen() throws Exception {
		for (int i = 0; i < 100; i++) {
			taskgraph();
		}
		System.out.println("\n" + "-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- ");
		for (int i = 0; i < 100; i++) {
			taskgraph();

			System.out.println("-- " + i + "\n");
		}

	}

}
