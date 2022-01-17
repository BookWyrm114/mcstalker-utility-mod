package com.mcstalker.utils.widgets;

import java.util.List;

public interface ISuggestionList {
	List<String> getValues();

	void onValueSelected(String value);

	default String getDefaultSuggestion() {
		return null;
	}
}