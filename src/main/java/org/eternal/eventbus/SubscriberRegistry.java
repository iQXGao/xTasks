package org.eternal.eventbus;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;

/***
 * 
 * 2018.03.07
 *
 */
public final class SubscriberRegistry {

	EventBus eb;
	Map<String, EventConsumer> registryMap = Maps.newConcurrentMap();

	SubscriberRegistry with(EventBus eb) {
		this.eb = eb;
		return this;
	}

	public <E extends Event> SubscriberRegistry register(Class<E> eventClz, EventBus.EventHandler handler) {
		return register(getTopicNameByEventClass(eventClz), handler.with(eb));
	}

	public SubscriberRegistry register(String topic, EventBus.EventHandler handler) {

		if (!Optional.ofNullable(registryMap.get(topic)).isPresent()) {
			this.registryMap.put(topic, new EventConsumer(handler));
		}

		return this;
	}

	public SubscriberRegistry unregister(String topic) {
		registryMap.remove(topic);
		return this;
	}

	public <E extends Event> SubscriberRegistry unregister(Class<E> eventClz) {

		return unregister(getTopicNameByEventClass(eventClz));
	}

	private <E extends Event> String getTopicNameByEventClass(Class<E> eventClz) {
		return eventClz.getName();
	}

	EventConsumer match(String topic) {
		return this.registryMap.get(topic);
	}

	EventConsumer match(Class<? extends Event> eventClz) {
		return match(getTopicNameByEventClass(eventClz));
	}

}
