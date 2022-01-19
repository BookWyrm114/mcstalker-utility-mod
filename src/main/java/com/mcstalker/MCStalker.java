package com.mcstalker;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcstalker.networking.Requests;
import com.mcstalker.networking.objects.Filters;
import com.mcstalker.utils.Skip;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

public class MCStalker implements ModInitializer {

	public static final String MODID = "mcstalker";
	public static final String NAME = "MCStalker";
	public static final MinecraftClient MC = MinecraftClient.getInstance();
	public static final Logger LOGGER = LogManager.getLogger(NAME);
	public static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

	public static final ModMetadata MOD_METADATA = FabricLoader.getInstance().getModContainer(MODID).orElseThrow().getMetadata();
	public static final CustomValue.CvObject MCSTALKER_METADATA = MOD_METADATA.getCustomValue("mcstalker").getAsObject();

	public static final OkHttpClient OKHTTP_CLIENT = new OkHttpClient.Builder()
			.addInterceptor(chain -> {
				String friendlyString = MOD_METADATA.getVersion().getFriendlyString();
				// somehow some versions didn't properly read that, no idea why
				if (friendlyString.equals("${version}"))
					friendlyString = "dev";
				Request originalRequest = chain.request();
				Request withUserAgent = originalRequest.newBuilder()
						.header("User-Agent", "MCStalker-Fabric/" + friendlyString)
						.build();
				return chain.proceed(withUserAgent);
			})
			.build();

	public static final Gson GSON = new GsonBuilder()
			.excludeFieldsWithModifiers(Modifier.TRANSIENT)
			.registerTypeAdapterFactory(RecordTypeAdapterFactory.builder()
					.allowUnknownProperties()
					.allowMissingComponentValues()
					.create())
			.addSerializationExclusionStrategy(new ExclusionStrategy() {
				@Override
				public boolean shouldSkipField(FieldAttributes f) {
					return f.getAnnotation(Skip.class) != null && f.getAnnotation(Skip.class).serialization();
				}

				@Override
				public boolean shouldSkipClass(Class<?> clazz) {
					return false;
				}
			})
			.addDeserializationExclusionStrategy(new ExclusionStrategy() {
				@Override
				public boolean shouldSkipField(FieldAttributes f) {
					return f.getAnnotation(Skip.class) != null && f.getAnnotation(Skip.class).deserialization();
				}

				@Override
				public boolean shouldSkipClass(Class<?> clazz) {
					return false;
				}
			})
			.create();

	/**
	 * Used for API communication
	 */
	public static final Gson GSON_REMAPPED = GSON.newBuilder()
			// thanks GSON for not allowing you to register type adapters for generic interfaces
			.registerTypeAdapter(Filters.AscDesc.class, new Filters.TypeAdapter())
			.registerTypeAdapter(Filters.AuthStatus.class, new Filters.TypeAdapter())
			.registerTypeAdapter(Filters.Country.class, new Filters.TypeAdapter())
			.registerTypeAdapter(Filters.MinecraftVersion.class, new Filters.MinecraftVersion.TypeAdapter())
			.registerTypeAdapter(Filters.SortMode.class, new Filters.TypeAdapter())
			.registerTypeAdapter(Filters.WhiteListStatus.class, new Filters.TypeAdapter())
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
		Requests.getServers(response -> {
			if (response == null) {
				LOGGER.error("Server filtering failed!");
			} else if (response.isRatelimited()) {
				LOGGER.error("Server filtering failed due to rate limiting!");
			}
		});
		LOGGER.info("Mod started!");

		DiscordRP.start();

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			while (!toExecute.isEmpty()) {
				toExecute.poll().run();
			}
		});
	}
}