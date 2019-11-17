package org.eternal.util;

public final class Timer {

	static public String exchange(long totalTime) {
		long hour = 0;
		long minute = 0;
		long second = 0;
		second = totalTime / 1000;
		if (totalTime <= 1000 && totalTime > 0) {
			second = 1;
		}
		if (second > 60) {
			minute = second / 60;
			second = second % 60;
		}
		if (minute > 60) {
			hour = minute / 60;
			minute = minute % 60;
		}
		String duration = (hour >= 10 ? hour : "0" + hour) + ":" + (minute >= 10 ? minute : "0" + minute) + ":"
				+ (second >= 10 ? second : "0" + second);
		return duration;
	}
}
