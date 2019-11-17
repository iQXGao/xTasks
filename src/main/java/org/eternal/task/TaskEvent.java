package org.eternal.task;

import java.util.Map;

import org.eternal.eventbus.Event;

import com.google.common.collect.Maps;



public interface TaskEvent extends Event {

	enum DefPropertyKeys {
		_Path("_$Path$_", false, "_"),

		_Content("_$Content$_", true, "content"),

		_Args("_$Args$_", true, "args"),

		_None("_$^_$^_$", false, "_");

		final String key;

		final boolean flag;
		final String alias;

		DefPropertyKeys(String key, boolean flag, String alias) {
			this.key = key;
			this.flag = flag;
			this.alias = alias;
		}

		static DefPropertyKeys isDefKey(String key) {

			for (DefPropertyKeys defKey : values()) {
				if (key.equals(defKey.key)) {
					return defKey;
				}
			}
			return _None;
		}

	}

	static TaskEvent generate() {
		return new Impl() {

		};
	}

	<E extends TaskEvent, T> E add(String key, T object);

	default <T> TaskEvent withContent(T args) {
		return add(DefPropertyKeys._Content.key, args);
	}

	default <T> T getContent() {
		return this.getProperty(DefPropertyKeys._Content.key);
	}

	default <T> TaskEvent withArgs(T args) {
		return add(DefPropertyKeys._Args.key, args);
	}

	default <T> T getArgs() {
		return this.getProperty(DefPropertyKeys._Args.key);
	}

	default TaskEvent withPath(String path) {
		return add(DefPropertyKeys._Path.key, path);

	}

	default String getPath() {
		return this.getProperty(DefPropertyKeys._Path.key);
	}

	<E extends TaskEvent> E duplicate();

	class Errors extends Impl {

	}

	abstract class Impl implements TaskEvent {

		Map<String, Object> cache = null;

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getProperty(String key) {
			return cache == null ? null : (T) cache.get(key);
		}

		private <T> void _add(String key, T object) {
			if (cache == null) {
				this.cache = Maps.newConcurrentMap();
			}
			cache.put(key, object);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <E extends TaskEvent, T> E add(String key, T object) {
			_add(key, object);
			return (E) this;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <E extends TaskEvent> E duplicate() {
			Impl impl = new Impl() {
			};
			impl.cache.putAll(impl.cache);
			return (E) impl;
		}

		@Override
		public String toString() {
			String _cacheValue = "Null";
			if (cache != null) {
				_cacheValue = "";

				for (String key : cache.keySet()) {
					DefPropertyKeys defKey = DefPropertyKeys.isDefKey(key);
					if (defKey != DefPropertyKeys._None) {
						if (defKey.flag) {
							_cacheValue += defKey.alias + ":" + cache.get(key) + ",";
						}
					} else {
						_cacheValue += key + ":" + cache.get(key) + ",";
					}
				}
			}
			return "{" + "cache:" + _cacheValue + "} ";
		}
	}

}
