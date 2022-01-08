package com.mcstalker.setting;

public class Settings {

	public static final Setting<String> apiKey = add(new Setting<>("API Key", null, String.class));

	private static <T> Setting<T> add(Setting<T> setting) {
		SettingsManager.addSetting(setting);
		return setting;
	}
}