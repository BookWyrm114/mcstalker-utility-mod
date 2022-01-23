package com.mcstalker.utils.widgets;

import com.mcstalker.utils.CaseInsensitiveArrayList;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class SuggestionTextField extends TextFieldWidget {

	private final ISuggestionList suggestionList;
	private final CaseInsensitiveArrayList suggestions;

	public SuggestionTextField(TextRenderer textRenderer, int x, int y, int width, int height, Text text, ISuggestionList suggestions) {
		super(textRenderer, x, y, width, height, text);
		this.setText(text.asString());
		this.suggestionList = suggestions;
		this.suggestions = new CaseInsensitiveArrayList(suggestions.getValues());
		this.onChanged = value -> {
			this.setSuggestion(getSuggestion(value));
			this.setEditableColor(this.suggestions.containsIgnoreCase(value) ? 0xFFFFFF : 0xFF0000);
		};
		this.setChangedListener(null);
	}

	@Nullable
	private String getSuggestion(String text) {
		if (text == null || text.isEmpty()) return suggestionList.getDefaultSuggestion();
		String t = this.suggestions.startsWithIgnoreCase(text);
		return t != null ? t.substring(text.length()) : null;
	}

	private final Consumer<String> onChanged;

	@Override
	public void setChangedListener(Consumer<String> changedListener) {
		super.setChangedListener(valRaw -> {
			String ignoreCase = this.suggestions.getIgnoreCase(valRaw);

			onChanged.accept(valRaw);

			if (changedListener != null) {
				changedListener.accept(valRaw);
			}

			if (ignoreCase != null)
				suggestionList.onValueSelected(ignoreCase);
		});
	}

	public boolean onTabPressed() {
		String suggestion = this.getSuggestion(getText());
		if (suggestion != null && !suggestion.isEmpty() && !suggestion.equals(getText())) {
			this.setSuggestion(null);
			this.setText(getText() + suggestion);
			return true;
		}
		return false;
	}
}