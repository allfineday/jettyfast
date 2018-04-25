package com.jettyweb.util;

public class Assert {

	public static void notNull(Object obj) {
		if (obj == null) {
			throw new RuntimeException("param must not be null");
		}

	}

	public static void notNull(Object obj, String msg) {
		if (obj == null) {
			throw new RuntimeException(msg);
		}

	}

	public static void isTrue(boolean b, String msg) {
		if (b) {
			return;
		}
		throw new RuntimeException(msg);
	}

	public static void hasText(String text, String msg) {
		if (text == null || text.trim().isEmpty()) {
			throw new RuntimeException(msg);
		}

	}

}
