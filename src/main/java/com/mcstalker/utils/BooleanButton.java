package com.mcstalker.utils;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class BooleanButton extends ButtonWidget {

	private boolean value;
	private final Text initialMessage;

	public BooleanButton(int x, int y, int width, int height, Text message, PressAction onPress, boolean value) {
		super(x, y, width, height, Text.of(message.getString().replace("%value%", value ? "§aON" : "§cOFF")), onPress);
		this.initialMessage = message;
		this.value = value;
	}

	public boolean getValue() {
		return this.value;
	}

	@Override
	protected boolean clicked(double mouseX, double mouseY) {
		if (this.active && this.isHovered()) {
			value = !value;
			this.setMessage(Text.of(initialMessage.getString().replace("%value%", value ? "§aON" : "§cOFF")));
			onPress();
			return true;
		}
		return false;
	}
}