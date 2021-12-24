package souper.mcstalker.client.api;

import com.google.gson.*;
import souper.mcstalker.client.FilterOptionsScreen;
import souper.mcstalker.client.MCStalkerServerList;
import souper.mcstalker.client.api.pojo.MCSServer;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class MCStalkerAPIWrapper
{
    public enum SortingOrder
    {
        NEW("new"),
        UPDATED("updated"),
        EMPTY("empty"),
        TOP("top");

        private final String method;

        SortingOrder(String val)
        {
            this.method = val;
        }

        public final String getSortingOrder()
        {
            return this.method;
        }
    }

    public enum ASCDESC
    {
        ASC("ASC"),
        DESC("DESC");

        private final String ascdesc;

        ASCDESC(String m)
        {
            this.ascdesc = m;
        }

        public final String getAscdesc()
        {
            return this.ascdesc;
        }
    }

    public static ArrayList<MCSServer> getServers(SortingOrder sortingOrder, ASCDESC ascdesc)
    {
        return getServers(sortingOrder, ascdesc, true, true);
    }

    private static ServerInfo addServer(String ip)
    {
        String actualIP  = ip;

        if(ip.contains(":"))
        {
            actualIP = ip.split(":")[0];
        }

        return new ServerInfo(actualIP, ip, false);
    }

    public static MCStalkerServerList refresh()
    {
        try
        {
            Callable<ArrayList<MCSServer>> makeCall = () -> MCStalkerAPIWrapper.getServers(FilterOptionsScreen.getSortingOrder(),
                    FilterOptionsScreen.getAscdesc());
            MCStalkerServerList serverList = new MCStalkerServerList(MinecraftClient.getInstance());
            for(MCSServer serv : makeCall.call())
            {
                serverList.add(addServer(serv.getIP()));
            }

            return serverList;
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<MCSServer> getServers(SortingOrder sortingOrder, ASCDESC ascdesc, boolean vanillaOnly, boolean mustHavePeople)
    {
        ArrayList<MCSServer> result = new ArrayList<MCSServer>();

        final int protocolVer = SharedConstants.getGameVersion().getProtocolVersion();

        JsonObject obj = new JsonObject();

        obj.add("country", new JsonPrimitive(FilterOptionsScreen.getCountry()));
        obj.add("version", new JsonPrimitive(protocolVer));
        obj.add("ascdesc", new JsonPrimitive(ascdesc.getAscdesc()));
        obj.add("sortMode", new JsonPrimitive(sortingOrder.getSortingOrder()));
        obj.add("mustHavePeople", new JsonPrimitive(mustHavePeople));
        obj.add("vanillaOnly", new JsonPrimitive(vanillaOnly));
        obj.add("page", new JsonPrimitive(1));
        obj.add("searchText", new JsonPrimitive(""));

        final String json = new Gson().toJson(obj);

        System.out.println(json);

        try
        {
            HttpClient httpClient    = HttpClientBuilder.create().build();
            HttpPost     post          = new HttpPost("https://backend.mcstalker.com/api/filterservers");
            StringEntity postingString = new StringEntity(json);
            post.setEntity(postingString);
            post.setHeader("Content-type", "application/json");
            HttpResponse response = httpClient.execute(post);

            JsonObject jsonResult = new JsonParser().parse(new InputStreamReader(response.getEntity().getContent())).getAsJsonObject();

            JsonArray arr = jsonResult.get("result").getAsJsonArray();

            for(JsonElement elem : arr)
            {
                JsonObject arrObj = elem.getAsJsonObject();

                result.add(new MCSServer(arrObj.get("createdAt").getAsLong(), arrObj.get("updatedAt").getAsLong(),
                        arrObj.get("protocol").getAsInt(), arrObj.get("online").getAsInt(), arrObj.get("max").getAsInt(),
                        arrObj.get("ipInfo").getAsJsonObject().get("country").getAsString(), arrObj.get("alive").getAsBoolean(),
                        arrObj.get("vanilla").getAsBoolean(), arrObj.get("ip").getAsString()));
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }


        return result;
    }
}
