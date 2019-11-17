package org.eternal.task.builder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eternal.eventbus.EventBus;
import org.eternal.task.builder.Builder.FlowBuilder;
import org.eternal.task.handler.TaskHandler;
import org.eternal.task.router.Router;

/***
 * 
 * @author gaoqi
 *
 */

interface Meta {

	String getKey();

	<T> T getObject();

	<Ext> Ext getExtObject();

	<Ext> Meta withExtObject(Ext extObject);

	class Meta4Handler implements Meta {

		final String key;
		final TaskHandler handler;
		Object extObject;

		Meta4Handler(String key, TaskHandler handler) {
			this.key = key;
			this.handler = handler;
		}

		@Override
		public String getKey() {
			return key;
		}

		@SuppressWarnings("unchecked")
		@Override
		public TaskHandler getObject() {
			return handler;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <Ext> Ext getExtObject() {

			return (Ext) extObject;
		}

		@Override
		public <Ext> Meta withExtObject(Ext extObject) {
			this.extObject = extObject;
			return this;
		}

	}

	class Meta4Flow implements Meta {

		final String key;
		final FlowBuilder fb;
		Object extObject;

		Meta4Flow(String key, FlowBuilder fb) {
			this.key = key;
			this.fb = fb;
		}

		@Override
		public String getKey() {
			return key;
		}

		@SuppressWarnings("unchecked")
		@Override
		public FlowBuilder getObject() {
			return fb;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <Ext> Ext getExtObject() {
			return (Ext) extObject;
		}

		@Override
		public <Ext> Meta withExtObject(Ext extObject) {
			this.extObject = extObject;
			return this;
		}
	}

	static Meta4Flow flower(String key, FlowBuilder object) {
		return new Meta4Flow(key, object);
	}

	static Meta4Handler handler(String key, TaskHandler object) {

		return new Meta4Handler(key, object);
	}

	abstract class Ware {

		EventBus eb = EventBus.generate();

		Map<String, Object> routes = new ConcurrentHashMap<>();

		abstract protected <T> Ware build(T args);

		final Router r;

		Ware(final Router r) {
			this.r = r;
		}
	}

}
