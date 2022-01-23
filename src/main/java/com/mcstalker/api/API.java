package com.mcstalker.api;

import com.mcstalker.mixin.MinecraftClientAccessor;
import net.minecraft.client.util.Session;
import net.minecraft.text.Text;

import java.util.Optional;

import static com.mcstalker.MCStalker.MC;

public class API {

	private static BanEvadeButton banEvadeButton = (buttonWidget) -> {
		final String rawUsername = MC.getSession().getUsername();
		final String username = " " + (rawUsername.length() > 14 ? rawUsername.substring(0, 14) : rawUsername) + " ";
		((MinecraftClientAccessor) MC).setSession(new Session(username, "", "", Optional.empty(), Optional.empty(), Session.AccountType.MOJANG));
		buttonWidget.setMessage(Text.of("\"" + username + "\""));
		buttonWidget.active = false;
	};

	/**
	 * Set this in your API class to override the default alt button.
	 * @param banEvadeButton the implementation of {@link com.mcstalker.api.BanEvadeButton} to use
	 */
	@SuppressWarnings("unused")
	// ^ will only get accessed in clients / other mods and not in this project and thus the warning is unnecessary
	public static void setBanEvadeButton(BanEvadeButton banEvadeButton) {
		API.banEvadeButton = banEvadeButton;
	}

	public static BanEvadeButton getBanEvadeButton() {
		return banEvadeButton;
	}
}