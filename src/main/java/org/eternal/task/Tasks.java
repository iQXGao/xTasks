package org.eternal.task;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.eternal.task.builder.Builder;
import org.eternal.task.builder.Builder.FlowBuilder;
import org.eternal.task.builder.Builder.TaskBuilder;
import org.eternal.task.router.Router;


public interface Tasks {

	enum Grade {
		Non, Init, Pending, Working, Waiting, Error
	}

	static Tasks generate(String code, Consumer<TaskBuilder> consumer) throws Exception {
		TaskBuilder builder = TaskBuilder.generate(code);
		consumer.accept(builder);
		return new TasksImpl().init(((Builder) builder).build());
	}

	static Tasks flow(String name, Consumer<FlowBuilder> consumer) throws Exception {
		FlowBuilder builder = FlowBuilder.generate(name);
		consumer.accept(builder);
		return new TasksImpl().init(((Builder) builder).build());
	}

	static Tasks generate(Builder... builders) throws Exception {
		return new TasksImpl().init(Arrays.asList(builders));
	}

	TaskAssist accept(TaskEvent event) throws Exception;

	TaskAssist accept(String key) throws Exception;
	// --------------------------------------------------------------------------------------

	final class TasksImpl implements Tasks {

		final Map<String, Router> routes = new ConcurrentHashMap<>();

		<E> Router isBadRequest(E request) {
			String key;
			if (request instanceof TaskEvent) {
				TaskEvent event = (TaskEvent) request;
				key = Optional.ofNullable(event.getPath()).map(path -> {
					String[] keys = path.split("\\.");
					if (keys.length > 1) {
						return keys[0];
					}
					return path;
				}).orElse(event.getClass().getName());
			} else {
				key = request.toString();
			}
			return Optional.ofNullable(routes.get(key)).map(router -> {
				if (router.isStarting()) {
					return null;
				}
				return router;
			}).orElse(null);

		}

		@Override
		public TaskAssist accept(TaskEvent event) throws Exception {
			return new TaskAssist(event).withTask(this);
		}

		@Override
		public TaskAssist accept(String key) throws Exception {
			return new TaskAssist(key).withTask(this);
		}

		TasksImpl init(Router router) {
			this.routes.put(router.getName(), router);
			return this;
		}

		TasksImpl init(Router... ls) {
			for (Router r : ls) {
				init(r);
			}
			return this;
		}

		TasksImpl init(List<Builder> ls) throws Exception {
			for (Builder b : ls) {
				init(b.build());
			}
			return this;
		}

		TasksImpl() {
		}

	}

}
