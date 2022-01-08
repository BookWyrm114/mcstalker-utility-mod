package com.mcstalker;

import ca.weblite.objc.Client;
import com.google.common.io.Files;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ConfigManager {

	private static ConfigManager INSTANCE;

	public static ConfigManager getInstance() {
		return INSTANCE;
	}

	public File configFile;
	private JSONObject json;

	public ConfigManager() {
		INSTANCE = this;
		this.configFile = new File(MCStalker.MODID + "/config.json");
		loadSettings();
	}

	public void loadSettings() {
		if (!configFile.exists()) {
			this.json = new JSONObject();
			writeConfigToFile();
			return;
		}
		try {
			this.json = new JSONObject(Files.asCharSource(configFile, StandardCharsets.UTF_8).read());
		} catch (Exception e) {
			e.printStackTrace();
			this.json = new JSONObject();
		}
	}

	/**
	 * Set to the beginning of unix time so that the first file write won't get delayed
	 */
	private long lastFileWrite = 0;
	private ScheduledFuture<?> future;

	public void writeConfigToFile() {
		if (System.currentTimeMillis() - lastFileWrite <= 5000) {
			// write every 5 seconds at max, schedule a task to write after 5 seconds
			if (future == null || future.isDone()) {
				future = MCStalker.scheduledExecutor.schedule(this::forceWrite, System.currentTimeMillis() - lastFileWrite, TimeUnit.MILLISECONDS);
			}
			return;
		}
		if (future != null) future.cancel(false);
		forceWrite();
	}

	/**
	 * Don't call this, its dangerous! (might produce ConcurrentWriteException)
	 */
	public synchronized void forceWrite() {
		try {
			lastFileWrite = System.currentTimeMillis();
			Files.asCharSink(configFile, StandardCharsets.UTF_8).write(this.json.toString(2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}