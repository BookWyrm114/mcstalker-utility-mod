package com.mcstalker.api;

import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.widget.ButtonWidget;

/**
 * Ban Evade Button - a button that renders in the DisconnectedScreen and per default changes your cracked username to " name "
 *
 * This still has to be improved!
 */
public interface BanEvadeButton {
	default boolean shouldRenderDefaultButton() {
		 return true;
	}

	void onClick(ButtonWidget button);

	@SuppressWarnings("all")
	default void addCustomButton(DisconnectedScreen disconnectedScreenMixin) {};
}