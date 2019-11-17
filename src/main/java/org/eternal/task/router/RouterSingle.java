package org.eternal.task.router;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.eternal.async.F;
import org.eternal.eventbus.EventResult;
import org.eternal.task.TaskEvent;

/***
 * 
 * @author gaoqi
 *
 *
 *
 *
 */
class RouterSingle extends RouterImpl {

	final Map<String, Object> routes = new ConcurrentHashMap<>();

	RouterSingle(String name) {
		super(name);
	}

	@SuppressWarnings("unchecked")
	<T> T transfer(TaskEvent event) {
		String key = Optional.ofNullable(event.getPath()).map(path -> {
			String[] keys = path.split("\\.");
			if (keys.length > 1) {
				int tL = keys[0].length();
				return path.substring(tL + 1, path.length());
			}
			return path;
		}).orElseGet(() -> {
			return event.getClass().getName();
		});
		return (T) Optional.ofNullable(getRouter(key)).orElse(getRouter(event.getClass().getName()));
	}

	@Override
	public void accept(TaskEvent event) {
		Optional.ofNullable(transfer(event)).ifPresent(args -> {
			if (args instanceof Router) {
				((Router) args).accept(event);
				return;
			} else if (Null.equals(args)) {
				feedback(event);
			} else {
				feedback(event.withPath(getPath(args.toString())));
			}
		});

	}

	@Override
	public void accept(TaskEvent event, Consumer<EventResult> then) {

		if (!Optional.ofNullable(transfer(event)).map(args -> {
			if (args instanceof Router) {
				((Router) args).accept(event, then);
			} else if (Null.equals(args)) {
				feedback(event, then);
			} else {
				feedback(event.withPath(getPath(args.toString())), then);
			}
			return true;
		}).orElse(false))

			Optional.ofNullable(then).ifPresent(call -> {
				call.accept(EventResult.Error("Event " + event.toString() + " not match . "));
			});
	}

	// --- only match first time ;
	protected void feedback(TaskEvent event, Consumer<EventResult> call) {

		F.async(send(event), new F.Callback<EventResult>() {
			@Override
			public void onFailure(Throwable e) {
				call.accept(EventResult.Error(e.getMessage()));

			}

			@Override
			public void onSuccess(EventResult result) {
				call.accept(result);
			}

		});
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <V> Router init(V args) {
		this.routes.putAll((Map<String, Object>) args);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	<T> T getRouter(String key) {
		return (T) this.routes.get(key);
	}

}
