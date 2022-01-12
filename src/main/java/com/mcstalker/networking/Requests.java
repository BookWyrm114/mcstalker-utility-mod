package com.mcstalker.networking;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mcstalker.networking.objects.FilterServerResponse;
import com.mcstalker.networking.objects.FilterServersRequest;
import com.mcstalker.networking.objects.Server;
import com.mcstalker.setting.Settings;
import com.mcstalker.utils.RateLimitedException;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.mcstalker.MCStalker.*;
import static com.mcstalker.networking.objects.FilterProperties.*;

public class Requests {

    private static final String REQUEST_URL = "https://backend.mcstalker.com/api/";

	private static final LoadingCache<FilterServersRequest, FilterServerResponse> serversCache = CacheBuilder.newBuilder()
			.expireAfterWrite(1, TimeUnit.MINUTES)
			.build(new CacheLoader<>() {
				@Override
				public @NotNull FilterServerResponse load(@NotNull FilterServersRequest filterServersRequest) throws IOException, RateLimitedException {
					JSONObject jsonAsObj = new JSONObject(GSON_REMAPPED.toJson(filterServersRequest));
					// ugly workaround to fix gson
					jsonAsObj.remove("version");
					jsonAsObj.put("version", version.protocolId() == -1 ? "all" : version.getRemapped());
					String json = jsonAsObj.toString();

					RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json"));

					Request.Builder requestBuilder = new Request.Builder()
							.url(REQUEST_URL + "filterservers")
							.post(requestBody);

					if (Settings.apiKey.getValue() != null) {
						requestBuilder.addHeader("Authorization", "Bearer " + Settings.apiKey.getValue());
					}

					Request request = requestBuilder.build();

					Call call = OKHTTP_CLIENT.newCall(request);
					LOGGER.info("Requesting servers with filter: " + json);
					Response response = call.execute();

					if (response.code() != 200) {
						if (response.code() == 429) {
							LOGGER.error("Got ratelimited!");
							throw new RateLimitedException();
						}
						LOGGER.error("Error while requesting servers: " + response.code() + " " + response.message());
						throw new IOException("Error while requesting servers: " + response.code() + " " + response.message());
					}

					assert response.body() != null;

					List<Server> servers = new ArrayList<>();
					JSONObject body = new JSONObject(response.body().string());
					JSONArray serversArr = body.getJSONArray("result");
					for (int i = 0; i < serversArr.length(); i++) {
						JSONObject jsonObject = serversArr.getJSONObject(i);
						try {
							servers.add(GSON.fromJson(jsonObject.toString(), Server.class));
						} catch (Exception e) {
							// too big to display
							jsonObject.remove("favicon");
							LOGGER.error("Error while parsing server: " + jsonObject + ": " + e.getMessage());
						}
					}
					return new FilterServerResponse(body.getInt("page"), body.getInt("perPage"), body.getInt("remainingPages"), servers.toArray(Server[]::new));
				}
			});

    public static @Nullable FilterServerResponse filterServers(FilterServersRequest filterServersRequest) throws NullPointerException, RateLimitedException {
		try {
			return serversCache.get(filterServersRequest);
		} catch (ExecutionException e) {
			LOGGER.error("Error while filtering servers: " + e.getMessage());
		}
		return null;
	}

    public static JSONArray getVersions() throws Exception {
        return new JSONArray(
                OKHTTP_CLIENT.newCall(
                        new Request.Builder()
                            .url(REQUEST_URL + "versions")
                            .build()
                )
                .execute()
                .body()
                .string()
            );
    }

	public static void getServers(Consumer<FilterServerResponse> callback) {
		scheduledExecutor.submit(() -> {
			try {
				callback.accept(getServers());
			} catch (NullPointerException e) {
				LOGGER.error("Error while requesting servers: " + e.getMessage());
				callback.accept(null);
			} catch (RateLimitedException e) {
				callback.accept(FilterServerResponse.RATELIMITED);
			}
		});
	}

	public static FilterServerResponse getServers() throws NullPointerException, RateLimitedException {
		return getServers(page);
	}

    public static FilterServerResponse getServers(int page) throws NullPointerException, RateLimitedException {
        return filterServers(
            new FilterServersRequest(
                sortMode,
                ascdesc,
                version,
                country,
                mustHavePeople,
                vanillaOnly,
                searchText,
                page,
                whiteListStatus,
                authStatus
            )
        );
    }
}