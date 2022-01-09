package com.mcstalker;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcstalker.networking.Requests;
import com.mcstalker.networking.objects.FilterServerResponse;
import com.mcstalker.networking.objects.FilterServersRequest;
import com.mcstalker.networking.objects.Filters;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.MinecraftClient;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

public class MCStalker implements ModInitializer {

	public static final String MODID = "mcstalker";
	public static final String NAME = "MCStalker";
	public static final MinecraftClient MC = MinecraftClient.getInstance();
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
	public static final OkHttpClient OKHTTP_CLIENT = new OkHttpClient();
	public static final Gson GSON_REMAPPED = new GsonBuilder()
			.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT)
			// thanks GSON for not allowing you to register type adapters for generic interfaces
			.registerTypeAdapter(Filters.AscDesc.class, new Filters.TypeAdapter())
			.registerTypeAdapter(Filters.AuthStatus.class, new Filters.TypeAdapter())
			.registerTypeAdapter(Filters.Country.class, new Filters.TypeAdapter())
			.registerTypeAdapter(Filters.MinecraftVersion.class, new Filters.TypeAdapter())
			.registerTypeAdapter(Filters.SortMode.class, new Filters.TypeAdapter())
			.registerTypeAdapter(Filters.WhiteListStatus.class, new Filters.TypeAdapter())
			.registerTypeAdapterFactory(RecordTypeAdapterFactory.DEFAULT)
			.create();
	public static final Gson GSON = new GsonBuilder()
			.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT)
			.registerTypeAdapterFactory(RecordTypeAdapterFactory.DEFAULT)
			.create();

	public static final Queue<Runnable> toExecute = new LinkedBlockingQueue<>();

	@Override
	public void onInitialize() {
		new ConfigManager();

		scheduledExecutor.submit(() -> {
			try {
				LOGGER.info("Requesting versions...");
				Filters.setAvailableMojangVersions(Requests.getVersions());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		LOGGER.info("Testing server filtering...");
		Requests.getServers(LOGGER::info);
		LOGGER.info("Mod started!");

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			while (!toExecute.isEmpty()) {
				toExecute.poll().run();
			}
		});
	}
}
