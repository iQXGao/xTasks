package org.eternal.eventbus;

import java.util.concurrent.Callable;

import org.eternal.async.F;

/***
 * 
 * 2018.03.07
 *
 */

public final class EventConsumer {

	static EventConsumer DefErrorConsumer = new EventConsumer(new EventBus.EventHandler() {
		@Override
		protected <E extends Event> EventResult handle(E event) {
			return EventResult.Error(null, event.getClass().getName() + " no match handler .");
		}
	});

	String eventName;

	final EventBus.EventHandler handler;

	EventConsumer(EventBus.EventHandler handler) {
		this.handler = handler;
		init();
	}

	void init() {

	}

	public F.Listenable<EventResult> accept(Event event) {
		return F.submit(new Callable<EventResult>() {
			@Override
			public EventResult call() throws Exception {
				return handler.apply(event);
			}
		});
		// immediate(handler.apply(event));
	}

}
