package com.mcstalker;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.Locale;
import java.util.Objects;

/**
 * @author SIMULATAN | parts taken from Meteor Client
 */
public class DiscordRP {

	private static long created = 0;
	private static long ticksSinceUpdate = 0;
	private static Staff currentStaff = Staff.values()[0];

	private static final DiscordRichPresence rpc = new DiscordRichPresence();
	private static final DiscordRPC instance = DiscordRPC.INSTANCE;

	public static void start() {
		created = System.currentTimeMillis();

		final DiscordEventHandlers handlers = new DiscordEventHandlers();

		instance.Discord_Initialize(MCStalker.MCSTALKER_METADATA.get("discordappid").getAsString(), handlers, true, null);

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			boolean update = false;

			if (ticksSinceUpdate >= 100) {
				currentStaff = currentStaff.nextStaff();
				ticksSinceUpdate = 0;
				update = true;
			} else
				ticksSinceUpdate++;

			if (!Objects.equals(firstLine, lastFirstLine) || !Objects.equals(secondLine, lastSecondLine)) {
				lastFirstLine = firstLine;
				lastSecondLine = secondLine;
				update = true;
			}

			if (update) {
				update();
			}
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(DiscordRP::shutdown);
	}

	public static String firstLine = "Stalkin' everyone!";
	private static String lastFirstLine;
	public static String secondLine = "Gettin' ready to troll some kids";
	private static String lastSecondLine;

	public static void shutdown(Object dummy) {
		instance.Discord_ClearPresence();
		instance.Discord_Shutdown();
	}

	private static void update() {
		rpc.smallImageKey = currentStaff.getImageKey();
		rpc.smallImageText = currentStaff.getName();

		rpc.details = firstLine;
		rpc.state = secondLine;
		rpc.startTimestamp = created;

		rpc.largeImageKey = "mcstalker_512x512";
		rpc.largeImageText = "MCStalker | discord.gg/mcstalker";

		instance.Discord_UpdatePresence(rpc);
	}

	private enum Staff {
		SIMULATAN("SIMULATAN"),
		SOUPER("Souper"),
		TOWU("TheOnlyWayUp");

		private final String name;

		Staff(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public String getImageKey() {
			return name.toLowerCase(Locale.ENGLISH);
		}

		public Staff nextStaff() {
			return values()[(ordinal() + 1) % values().length];
		}
	}
}