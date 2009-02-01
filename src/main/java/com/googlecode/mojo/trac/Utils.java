package com.googlecode.mojo.trac;

public class Utils {

	public static boolean isEmpty(String s) {
		if (s == null || "".equals(s)) {
			return true;
		}
		return false;
	}

	public static void validateRequired(String key, String value) {
		if (isEmpty(value)) {
			throw new IllegalArgumentException("'" + key + "' is required.");
		}
	}
}
