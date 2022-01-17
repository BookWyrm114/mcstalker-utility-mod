package com.mcstalker.utils.widgets;

import com.mcstalker.networking.objects.Filters;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class EnumButton<T extends Enum<T>> extends ButtonWidget {

	private T value;
	private final Class<T> clazz;
	private final Text initialMessage;

	public EnumButton(int x, int y, int width, int height, Text message, PressAction onPress, T value, Class<T> clazz) {
		super(x, y, width, height, Text.of(message.getString().replace("%value%", value instanceof Filters.FancyName fancy ? fancy.getFancyName() : value.name())), onPress);
		this.value = value;
		this.clazz = clazz;
		this.initialMessage = message;
	}

	public T getValue() {
		return value;
	}

	@Override
	protected boolean clicked(double mouseX, double mouseY) {
		if (this.active && this.isHovered()) {
			value = clazz.getEnumConstants()[(value.ordinal() + 1) % clazz.getEnumConstants().length];
			this.setMessage(Text.of(initialMessage.getString().replace("%value%", value instanceof Filters.FancyName fancy ? fancy.getFancyName() : value.name())));
			onPress();
			return true;
		}
		return false;
	}
}