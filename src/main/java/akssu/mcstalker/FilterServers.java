package akssu.mcstalker;

import akssu.mcstalker.responseObjects.*;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;

public class FilterServers {


    public FilterServers() throws IOException {
    }

    public static FilterServerResponse filterServers() throws IOException {

        OkHttpClient client = new OkHttpClient();
        String json = "{\"sortMode\": \"updated\",\n" +
                "  \"ascdesc\": \"ASC\",\n" +
                "  \"version\": 757,\n" +
                "  \"country\": \"all\",\n" +
                "  \"mustHavePeople\": " + MCStalker.settings.getSettingByName("MustHavePlayersOnline").get() +",\n" +
                "  \"vanillaOnly\": true,\n" +
                "  \"searchText\": \"\",\n" +
                "  \"page\": 1,\n" +
                "  \"whiteListStatus\": 0,\n" +
                "  \"authStatus\": 0}";

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url("https://backend.mcstalker.com/api/filterservers")
                .post(body)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        Gson gson = new Gson();

        FilterServerResponse result1;
        assert response.body() != null;
        result1 = gson.fromJson(response.body().string(), FilterServerResponse.class);
        assert result1 != null;

        return result1;

    }


}



