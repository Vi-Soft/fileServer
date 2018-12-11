package com.visoft.utils;

public final class StringUtil {

	private StringUtil() {
		
	}
	
	public static boolean isEmpty(final String inStr) {
		return inStr == null || inStr.length() == 0;
	}
	
	public static String trim(final String inStr) {
		return !isEmpty(inStr) ? inStr.trim() : inStr;
	}
}
