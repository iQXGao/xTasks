package org.eternal.eventbus;

import java.util.Optional;

import org.eternal.async.F;

/***
 * 
 * 2018.03.07
 *
 */
public interface EventBus {

	F.Executor getExecutor();

	Sender sender();

	<E extends Event> EventBus register(Class<E> eventClz, EventBus.EventHandler handler);

	<E extends Event> EventBus register(String topic, EventBus.EventHandler handler);

	<E extends Event> EventBus unregister(Class<E> eventClz);

	static EventBus generate() {
		return EventBusImpl.instance;
	}

	abstract class EventHandler implements F.Function<Event, EventResult> {

		@Override
		public EventResult apply(Event event) {
			return handle(event);
		}

		protected abstract <E extends Event> EventResult handle(E event);

		protected Sender sender;

		EventHandler with(EventBus eb) {
			this.sender = eb.sender();
			return this;
		}

		protected <E extends Event> F.Listenable<EventResult> send(E event) {
			return Optional.ofNullable(sender).map(s -> s.send(event)).orElse(null);
		}

	}
}
