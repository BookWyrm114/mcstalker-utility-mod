package com.mcstalker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcstalker.networking.Requests;
import com.mcstalker.networking.objects.Filters;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	public static final ModMetadata MOD_METADATA = FabricLoader.getInstance().getModContainer(MODID).orElseThrow().getMetadata();

	public static final OkHttpClient OKHTTP_CLIENT = new OkHttpClient.Builder()
			.addInterceptor(chain -> {
				Request originalRequest = chain.request();
				Request withUserAgent = originalRequest.newBuilder()
						.header("User-Agent", "MCStalker-Fabric/" + MOD_METADATA.getVersion().getFriendlyString())
						.build();
				return chain.proceed(withUserAgent);
			})
			.build();

	public static final Gson GSON_REMAPPED = new GsonBuilder()
			.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT)
			// thanks GSON for not allowing you to register type adapters for generic interfaces
			.registerTypeAdapter(Filters.AscDesc.class, new Filters.TypeAdapter())
			.registerTypeAdapter(Filters.AuthStatus.class, new Filters.TypeAdapter())
			.registerTypeAdapter(Filters.Country.class, new Filters.TypeAdapter())
			.registerTypeAdapter(Filters.MinecraftVersion.class, new Filters.MinecraftVersion.TypeAdapter())
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
		Requests.getServers(response -> {
			if (response == null) {
				LOGGER.error("Server filtering failed!");
			} else if (response.isRatelimited()) {
				LOGGER.error("Server filtering failed due to rate limiting!");
			}
		});
		LOGGER.info("Mod started!");

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			while (!toExecute.isEmpty()) {
				toExecute.poll().run();
			}
		});
	}
}
