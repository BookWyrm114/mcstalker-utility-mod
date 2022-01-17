package com.mcstalker.utils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CaseInsensitiveArrayList extends ArrayList<String> {

	public CaseInsensitiveArrayList(List<String> list) {
		super(list);
	}

	public boolean startsWithIgnoreCase(String str) {
		for (String s : this) {
			if (s.toLowerCase(Locale.ENGLISH).startsWith(str.toLowerCase(Locale.ENGLISH))) {
				return true;
			}
		}
		return false;
	}

	public boolean containsIgnoreCase(String str) {
		for (String s : this) {
			if (s.equalsIgnoreCase(str)) {
				return true;
			}
		}
		return false;
	}

	@Nullable
	public String getIgnoreCase(String str) {
		for (String s : this) {
			if (s.equalsIgnoreCase(str)) {
				return s;
			}
		}
		return null;
	}
}