package org.eternal.eventbus;

import java.util.Optional;

import org.eternal.async.F.Listenable;

/***
 * 
 * 2018.03.07
 *
 */
public interface Dispatcher {

	Listenable<EventResult> dispatch(Event event, EventConsumer... consumers);

	interface Strategy {
		EventConsumer select(EventConsumer... consumers);
	}

	static Strategy getDefStrategy() {

		return new Strategy() {
			@Override
			public EventConsumer select(EventConsumer... consumers) {
				return consumers[0];
			}
		};
	}

	class Impl implements Dispatcher {

		Strategy strategy;

		private Strategy getStrategy() {
			return Optional.ofNullable(strategy).orElseGet(() -> {
				this.strategy = getDefStrategy();
				return strategy;
			});

		}

		@Override
		public Listenable<EventResult> dispatch(Event event, EventConsumer... consumers) {
			return Optional.ofNullable(getStrategy().select(consumers)).map(consumer -> {
				return consumer.accept(event);
			}).orElse(EventConsumer.DefErrorConsumer.accept(event));
		}
	}
}
