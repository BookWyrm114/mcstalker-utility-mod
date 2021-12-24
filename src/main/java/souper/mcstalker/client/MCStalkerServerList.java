package souper.mcstalker.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.ServerList;

// wrapper just to remove loadFile and saveFile
public class MCStalkerServerList extends ServerList
{

    public MCStalkerServerList(MinecraftClient client)
    {
        super(client);
    }

    @Override
    public void loadFile()
    {

    }

    @Override
    public void saveFile()
    {

    }
}
