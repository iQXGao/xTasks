package org.eternal.eventbus;

/***
 * 
 * 2018.03.07
 *
 */

public interface EventResult {

	<T> T getEntity();

	boolean isSuccessful();

	String getMessage();

	String getSourcePath();

	static EventResult Ok = EventResult.Success("Ok..");

	static EventResult Lock = EventResult.Success("Lock..");

	static EventResult UnLock = EventResult.Error("UnLock..");

	static <T> EventResult generate(T args, boolean flag, String message) {
		return new Impl(flag, args, message);
	}

	final class Impl implements EventResult {

		String _Path;

		Object entityObject;

		String message;

		boolean flag;

		<T> Impl(T args) {
			this(true, args);
		}

		<T> Impl(boolean flag, T args) {
			this(flag, args, "");
		}

		<T> Impl(boolean flag, T args, String message) {
			this.entityObject = args;
			this.flag = flag;
			this.message = message;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getEntity() {
			return (T) entityObject;
		}

		@Override
		public String getMessage() {
			return message;
		}

		@Override
		public boolean isSuccessful() {
			return flag;
		}

		@Override
		public String toString() {
			return "{'successful':" + isSuccessful() + "," + "'entity':"
					+ (getEntity() != null ? getEntity().toString() : "") + "," + "message:" + getMessage() + "}";
		}

		@Override
		public String getSourcePath() {
			return _Path;
		}

	}

	static <T> EventResult Success(T arg) {
		return generate(arg, true, "");
	}

	static <T> EventResult Error(T arg) {
		return Error(arg, "");
	}

	static <T> EventResult Error(T arg, String message) {
		return generate(arg, false, message);
	}

	static EventResult withSource(EventResult impl, String path) {
		Impl _impl = new Impl(impl.isSuccessful(), impl.getEntity(), impl.getMessage());
		_impl._Path = path;
		return _impl;
	}

}
