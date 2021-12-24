package souper.mcstalker.client;

import net.fabricmc.api.ClientModInitializer;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class McstalkerClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient() {
        System.out.println("init client lmao");
    }
}
