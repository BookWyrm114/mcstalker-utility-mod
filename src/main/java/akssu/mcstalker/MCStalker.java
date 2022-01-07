package akssu.mcstalker;

import akssu.mcstalker.setting.Setting;
import akssu.mcstalker.setting.SettingManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class MCStalker implements ModInitializer {

	public static final String MODID = "mcstkr";
	public static final String NAME = "MCStalker";
	public static SettingManager settings;
	public static final MinecraftClient MC = MinecraftClient.getInstance();
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	@Override
	public void onInitialize() {
		settings = new SettingManager();
		settings.addSetting(new Setting("MustHavePlayersOnline", true));
		settings.addSetting(new Setting("MustBeVanilla", true));
		FileManager.INSTANCE.loadSettingsList();

		LOGGER.info("Test to show FilterServers works");
		try {
			LOGGER.info(FilterServers.filterServers().perPage);
		} catch (IOException e) {
			e.printStackTrace();
		}

		LOGGER.info("Mod started :)");
	}
}
