package org.eternal.eventbus;

import java.util.Optional;

import org.eternal.async.F;
import org.eternal.async.F.Executor;
import org.eternal.async.F.Listenable;

/***
 * 
 * 2018.03.07
 *
 */

final class EventBusImpl implements EventBus {

	final static EventBusImpl instance = new EventBusImpl(new SubscriberRegistry(), F.getDefExecutor());

	final SubscriberRegistry registry;

	Dispatcher dispatcher = new Dispatcher.Impl() {

	};
	final F.Executor executor;

	final Sender sender = new Sender() {

		@Override
		public <E extends Event> Listenable<EventResult> send(E event) {
			return post(event);
		}
	};

	EventBusImpl(SubscriberRegistry registry, F.Executor executor) {
		this.registry = registry.with(this);
		this.executor = executor;
	}

	@Override
	public Executor getExecutor() {
		return executor;
	}

	<E extends Event> F.Listenable<EventResult> post(E event) {
		final String value = Optional.ofNullable(event.getProperty("_$Path$_")).map(s -> s.toString()).orElse("");
		return dispatcher.dispatch(event, "".equals(value) ? registry.match(event.getClass()) : registry.match(value));
	}

	@Override
	public Sender sender() {
		return sender;
	}

	@Override
	public <E extends Event> EventBus register(Class<E> eventClz, EventHandler handler) {
		registry.register(eventClz, handler);
		return this;
	}

	@Override
	public <E extends Event> EventBus unregister(Class<E> eventClz) {
		registry.unregister(eventClz);
		return this;
	}

	@Override
	public <E extends Event> EventBus register(String topic, EventHandler handler) {
		registry.register(topic, handler);
		return this;
	}

}
