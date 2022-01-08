package com.mcstalker.setting;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsManager {

	public static ArrayList<Setting<?>> settingsList = new ArrayList<>();
	public static HashMap<String, Setting<?>> settingsMap = new HashMap<>();

	public static void addSetting(Setting<?> setting) {
		settingsList.add(setting);
		settingsMap.put(setting.getName(), setting);
	}

    public static Setting<?> getSetting(String name) {
        return settingsMap.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T> Setting<T> getSetting(String id, Class<T> classOfT) {
        try {
            if (getSetting(id).getType().equals(classOfT))
                return (Setting<T>) getSetting(id);
        } catch (Exception ignored) {}
        return null;
    }
}