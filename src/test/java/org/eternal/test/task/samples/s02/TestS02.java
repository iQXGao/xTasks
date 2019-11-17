package org.eternal.test.task.samples.s02;

import org.eternal.task.Tasks;
import org.eternal.test.task.samples.TasksConfig;
import org.junit.Before;
import org.junit.Test;

public class TestS02 {

	static String taskcode = "test-task-02";
	static Tasks tasks;

	static String flowname = "sample-flow-handler-test";

	@Before
	public void init() {
		TasksConfig cfg = new TasksConfig(taskcode);
		cfg.addFlow(flowname, flow -> {
			flow
			.next(new FlowTestHandler("Flow-handler-A"))

            .next(new FlowTestHandler2("Flow-handler-X1"))
			.next(new FlowTestHandler("Flow-handler-B")) 
 
			.next(new FlowTestHandler("Flow-handler-C"))
            

			;
		});
		tasks = cfg.getTasks();
	}

	@Test
	public void tsFlowHandler() throws Exception {

	 
		tasks.accept(flowname)
				// -- content set
				.withContent("this is content test ")
				// -- args set
				.withArgs("{properity: s1,s2,s3,s4}")
				// -- param set
				.withParam("action1", "la la 1 ....").withParam("action2", "la la 2....")
				.withParam("action3", "la la 3....")
				// then set
				.then(result -> {
					System.out.println("--" + result.toString());
				});
	}

}
