package org.eternal.task.router;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.eternal.async.F;
import org.eternal.eventbus.EventBus;
import org.eternal.eventbus.EventResult;
import org.eternal.task.TaskEvent;

/***
 * 
 * @author gaoqi
 * 
 *         
 */
abstract class RouterImpl implements Router {

	protected AtomicBoolean starting = new AtomicBoolean(false);
	protected String path;
	final String name;
	final EventBus eb = EventBus.generate();
	protected Consumer<EventResult> then;
	TaskEvent eventHolder;

	RouterImpl(String name) {
		this.name = name;
	}

	abstract protected <V> Router init(V args);

	abstract <T> T getRouter(String key);

	@Override
	public boolean isStarting() {
		return starting.get();
	}

	@Override
	public String getPath() {
		return Optional.ofNullable(path).orElse(name);
	}

	@Override
	public String getName() {
		return name;
	}

	protected F.Listenable<EventResult> send(TaskEvent event) {
		return eb.sender().send(event);
	}

	protected void doError(Throwable e) {
		doError(EventResult.Error(e.getMessage()));
	}

	protected void doError(EventResult result) {
		this.starting.lazySet(false);
		Optional.ofNullable(then).ifPresent(f -> {
			f.accept(result);
			then = null;
			eventHolder = null;
		});
	}

	protected void doSuccess(EventResult result) {
		if (result.isSuccessful()) {
			doNext(result);
		} else {
			doError(result);
		}
	}

	protected void doNext(EventResult result) {
		String srcPath = result.getSourcePath();
		String key = srcPath;
		if (srcPath.startsWith(this.getName() + ".")) {
			int n = srcPath.indexOf(".");
			if (n > 0) {
				key = srcPath.substring(n + 1, srcPath.length());
			}
		}
		String target = Optional.ofNullable((String) getRouter(key)).map(t -> {
			return Null.equals(t) ? null : t;
		}).orElse(null);
		if (target != null) {
			TaskEvent _event = eventHolder == null ? TaskEvent.generate() : eventHolder;
			feedback(_event.withPath(target));
		} else {
			starting.lazySet(false);
			Optional.ofNullable(then).ifPresent(f -> {
				f.accept(result);
				then = null;
				eventHolder = null;
			});
		}
	}

	protected void feedback(TaskEvent event) {

		F.async(send(event), new F.Callback<EventResult>() {
			@Override
			public void onFailure(Throwable e) {
				doError(e);
			}

			@Override
			public void onSuccess(EventResult result) {
				doSuccess(result);
			}

		});
	}
}
