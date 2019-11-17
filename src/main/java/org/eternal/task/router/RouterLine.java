package org.eternal.task.router;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.eternal.eventbus.EventResult;
import org.eternal.task.TaskEvent;

/***
 * 
 * @author gaoqi
 * 
 */
class RouterLine extends RouterImpl {

	final EventResult runningFlag = EventResult.generate(null, true, " is running ,request ignore....");

	final Map<String, String> routes = new ConcurrentHashMap<>();

	RouterLine(String name) {
		super(name);
	}

	@Override
	public boolean neeLock() {
		return true;
	}

	@Override
	public void accept(TaskEvent event) {
		accept(event, null);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	protected <V> Router init(V args) {
		this.routes.putAll((Map<String, String>) args);
		this.path = routes.get(getName());
		return this;
	}

	@Override
	public void accept(TaskEvent event, Consumer<EventResult> then) {
		if (!starting.get()) {
			starting.set(true);
			this.then = then;
			eventHolder = event.withPath(this.getPath());
			feedback(eventHolder);
		} else {
			Optional.ofNullable(then).ifPresent(f -> {
				f.accept(runningFlag);
			});
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	String getRouter(String key) {
		return this.routes.get(key);
	}

}
