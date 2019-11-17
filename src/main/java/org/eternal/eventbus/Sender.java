package org.eternal.eventbus;

import org.eternal.async.F; 

public interface Sender {

	<E extends Event> F.Listenable<EventResult> send(E event);

}
