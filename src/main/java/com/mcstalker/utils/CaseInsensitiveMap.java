package com.mcstalker.utils;

import java.util.HashMap;
import java.util.Locale;

public class CaseInsensitiveMap<T> extends HashMap<String, T> {
	@Override
	public T put(String key, T value) {
		return super.put(key.toLowerCase(Locale.ENGLISH), value);
	}

	@Override
	public T get(Object key) {
		if (key instanceof String s) {
			return super.get(s.toLowerCase());
		} else {
			throw new IllegalArgumentException("Key is not a String!");
		}
	}
}