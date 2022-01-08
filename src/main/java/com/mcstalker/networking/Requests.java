package com.mcstalker.networking;

import com.mcstalker.MCStalker;
import com.mcstalker.networking.objects.FilterServerResponse;
import com.mcstalker.networking.objects.FilterServersRequest;
import com.mcstalker.networking.objects.Server;
import com.mcstalker.setting.Settings;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.mcstalker.MCStalker.*;
import static com.mcstalker.networking.objects.FilterProperties.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Requests {

    private static final String REQUEST_URL = "https://backend.mcstalker.com/api/";

    public static FilterServerResponse filterServers(FilterServersRequest filterServersRequest) throws IOException, NullPointerException {
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

    public static FilterServerResponse getServers() throws IOException {
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