package org.eternal.task.router;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eternal.eventbus.EventResult;
import org.eternal.task.TaskEvent;

/***
 * 
 * @author gaoqi
 * 
 * 
 */
public interface Router {

	static final String Null = "$NULL$";

	default boolean neeLock() {
		return false;
	}

	boolean isStarting();

	String getPath();

	default String getPath(String key) {
		return getName() + "." + key;
	}

	String getName();

	void accept(TaskEvent event);

	void accept(TaskEvent event, Consumer<EventResult> then);

	static <V> Router lineThen(String code, Function<Router, V> args) throws Exception {
		RouterLine r = new RouterLine(code);
		V value = Optional.ofNullable(args.apply(r)).orElseThrow(() -> new IllegalArgumentException());
		if (!(value instanceof Map)) {
			throw new IllegalArgumentException(" not support class type " + value.getClass());
		}
		return r.init(value);
	}

	static <V> Router singleThen(String code, Function<Router, V> args) throws Exception {
		RouterSingle r = new RouterSingle(code);
		V value = Optional.ofNullable(args.apply(r)).orElseThrow(() -> new IllegalArgumentException());
		if (!(value instanceof Map)) {
			throw new IllegalArgumentException(" not support class type " + value.getClass());
		}
		return r.init(value);
	}

	static <V> Router line(String code, V args) throws Exception {
		return new RouterLine(code).init(Optional.ofNullable(args).orElseThrow(() -> new IllegalArgumentException()));
	}

	static <V> Router single(String code, V args) throws Exception {
		return new RouterSingle(code).init(Optional.ofNullable(args).orElseThrow(() -> new IllegalArgumentException()));
	}

}
