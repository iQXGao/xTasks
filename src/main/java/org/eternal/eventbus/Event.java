package org.eternal.eventbus;

/***
 * 
 * 2018.03.07
 *
 */

public interface Event {

	<T> T getContent();

	<T> T getProperty(String key);

}
