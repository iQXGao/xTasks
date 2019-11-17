package org.eternal.task.handler;

import java.util.Optional;

import org.eternal.eventbus.Event;
import org.eternal.eventbus.EventBus;
import org.eternal.eventbus.EventResult;
import org.eternal.task.TaskEvent;

/****
 * 
 * @author gaoqi
 *
 */

public abstract class TaskHandler extends EventBus.EventHandler {

	protected <S extends TaskService> S getTaskService() {
		return getTaskService(Optional.empty());
	}

	abstract protected <V, S extends TaskService> S getTaskService(Optional<V> args);

	@Override
	protected <E extends Event> EventResult handle(E event) {
		TaskEvent evt = (TaskEvent) event;
		return EventResult.withSource(
				Optional.ofNullable((TaskService) getTaskService(Optional.ofNullable(evt.getArgs()))).map(service -> {
					return service.accept(evt);
				}).orElse(EventResult.Error(null, "no match handler service.")), evt.getPath());
	}

	// ----------------------------------------------

	static public class Errors extends TaskHandler {

		final TaskService defService = new TaskService.Impl() {

			@Override
			protected EventResult execute(TaskEvent event, EventResult result) {
				System.out.println("--error: {" + event.toString() + "}\n");
				return result;
			}
		}.withSender(sender);

		@SuppressWarnings("unchecked")
		@Override
		protected <V, S extends TaskService> S getTaskService(Optional<V> args) {
			return (S) defService;
		}

	}

}
