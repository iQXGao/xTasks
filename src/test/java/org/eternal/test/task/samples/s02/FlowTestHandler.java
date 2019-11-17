package org.eternal.test.task.samples.s02;

import java.util.Optional;

import org.eternal.eventbus.EventResult;
import org.eternal.task.TaskEvent;
import org.eternal.task.handler.FlowHandler;
import org.eternal.task.handler.TaskService;

public class FlowTestHandler extends FlowHandler {

	final String name;

	public FlowTestHandler(String name) {
		this.name = name;
	}

	@Override
	public String getFlowHandlerName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <V, S extends TaskService> S getTaskService(Optional<V> args) {

		args.ifPresent(s -> {
			System.out.println("-- " + getFlowHandlerName() + "  " + s.toString());
		});

		return (S) defService;
	}

	TaskService defService = new TaskService.Impl() {

		@Override
		protected EventResult execute(TaskEvent event, EventResult result) {
			System.out.println("-- flow test service action < " + getFlowHandlerName() + " >" + " Event:{"
					+ event.toString() + " }");
			return result;
		}
	};

}
