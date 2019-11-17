package org.eternal.task;

import java.util.function.Consumer;

import org.eternal.eventbus.EventResult;
import org.eternal.task.router.Router;

/****
 * 
 * @author gaoqi
 *
 */
public final class TaskAssist {

	final TaskEvent event;

	Tasks.TasksImpl tasks;

	TaskAssist(TaskEvent event) {
		this.event = event;
	}

	TaskAssist(String value) {
		this(TaskEvent.generate().withPath(value));
	}

	TaskAssist withTask(Tasks.TasksImpl tasks) {
		this.tasks = tasks;
		return this;
	}

	public TaskAssist withArgs(Object args) {
		event.withArgs(args);
		return this;
	}

	public TaskAssist withContent(Object content) {
		event.withContent(content);
		return this;
	}

	public <T> TaskAssist withParam(String key, T value) {
		event.add(key, value);
		return this;
	}

	final static public EventResult _Accepted = EventResult.generate(null, true, "accepted...");

	public EventResult then(Consumer<EventResult> after) {
		if (after == null) {
			return then();
		}
		Router router = tasks.isBadRequest(this.event);

		if (router == null) {
			return EventResult.Error(event, "unsupport event ... ");
		}
		router.accept(event, after);
		return _Accepted;
	}

	public EventResult then() {
		Router router = tasks.isBadRequest(this.event);

		if (router == null) {
			return EventResult.Error(event, "unsupport event ... ");
		}
		router.accept(event);
		return _Accepted;
	}

}
