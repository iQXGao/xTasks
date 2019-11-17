package org.eternal.task.handler;

import org.eternal.eventbus.EventResult;
import org.eternal.eventbus.Sender;
import org.eternal.task.TaskEvent;

/***
 * 
 * @author gaoqi
 * 
 * 
 */
public interface TaskService {

	EventResult accept(TaskEvent event);

	abstract class Impl implements TaskService {

		protected Sender sender;

		public Impl withSender(Sender sender) {
			this.sender = sender;
			return this;
		}

		protected EventResult before(TaskEvent event) {
			return EventResult.Ok;
		};

		protected abstract EventResult execute(TaskEvent event, EventResult result);

		protected EventResult locked(TaskEvent event) {
			return EventResult.UnLock;
		}

		protected EventResult after(TaskEvent event, EventResult result) {
			return result;
		};

		protected EventResult error(TaskEvent event, Exception ex) {
			return EventResult.Error(event, ex.getMessage());
		}

		@Override
		public EventResult accept(TaskEvent event) {
			EventResult result = locked(event);
			if (result.isSuccessful()) {
				return result;
			}
			try {
				result = before((TaskEvent) event);
				if (result.isSuccessful()) {
					result = execute((TaskEvent) event, result);
				}
			} catch (Exception ex) {
				result = error((TaskEvent) event, ex);
			} finally {
				result = after((TaskEvent) event, result);
			}
			return result;
		};
	}

}
