package com.mcstalker;

import com.google.common.io.Files;
import com.mcstalker.networking.objects.FilterProperties;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ConfigManager {

	private static ConfigManager INSTANCE;

	public static ConfigManager getInstance() {
		return INSTANCE;
	}

	public final File configFile;
	public final File filtersFile;
	private JSONObject json;

	public ConfigManager() {
		INSTANCE = this;
		this.configFile = new File(MCStalker.MODID + "/config.json");
		this.filtersFile = new File(MCStalker.MODID + "/filters.json");
		loadSettings();
	}

	public void loadSettings() {
		if (!configFile.exists()) {
			this.json = new JSONObject();
			writeConfigToFile();
			return;
		}
		try {
			if (configFile.exists())
				this.json = new JSONObject(Files.asCharSource(configFile, StandardCharsets.UTF_8).read());

			if (filtersFile.exists())
				MCStalker.GSON.fromJson(Files.asCharSource(filtersFile, StandardCharsets.UTF_8).read(), FilterProperties.class);
		} catch (Exception e) {
			e.printStackTrace();
			this.json = new JSONObject();
		}
	}

	private static final FilterProperties dummy = new FilterProperties();

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

	private static final Executor WRITE_EXECUTOR = Executors.newSingleThreadExecutor();

	/**
	 * Don't call this, it's dangerous! (might produce ConcurrentWriteException)
	 */
	public synchronized void forceWrite() {
		WRITE_EXECUTOR.execute(() -> {
			try {
				lastFileWrite = System.currentTimeMillis();
				configFile.getParentFile().mkdirs();
				filtersFile.getParentFile().mkdirs();
				Files.asCharSink(configFile, StandardCharsets.UTF_8).write(this.json.toString(2));
				Files.asCharSink(filtersFile, StandardCharsets.UTF_8).write(MCStalker.GSON.toJson(dummy));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}