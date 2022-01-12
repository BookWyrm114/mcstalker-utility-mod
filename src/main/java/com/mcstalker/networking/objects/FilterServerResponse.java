package com.mcstalker.networking.objects;

import com.mcstalker.MCStalker;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FilterServerResponse {

	public final int page;
	public final int perPage;
	public final int remainingPages;
	public final Server[] servers;

	private boolean ratelimited;

	public static final FilterServerResponse RATELIMITED = new FilterServerResponse(true);

	public boolean isRatelimited() {
		return ratelimited;
	}

	private FilterServerResponse(boolean ratelimited) {
		this(-1, -1, -1, null);
		this.ratelimited = ratelimited;
	}

	public FilterServerResponse(int page, int perPage, int remainingPages, Server[] servers) {
		this.page = page;
		this.perPage = perPage;
		this.remainingPages = remainingPages;
		this.servers = servers;
		this.ratelimited = false;
	}

private static final Pattern IP_REGEX = Pattern.compile("^((?:(?:\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])(?:\\.(?!:)|)){4}):(?!0)(\\d{1,4}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$");

	public ServerList getServerList() {
		MCStalkerServerList result = new MCStalkerServerList(MCStalker.MC);
		for (Server server : servers) {
			Matcher matcher = IP_REGEX.matcher(server.ip());
			// string comparison to avoid int parsing exception
			result.add(new ServerInfo(matcher.matches() && matcher.group(2) != null && matcher.group(2).equals("25565") ? matcher.group(1) : server.ip(), server.ip(), false));
		}
		return result;
	}

	@Override
	public String toString() {
		return "FilterServerResponse{" +
				"page=" + page +
				", perPage=" + perPage +
				", remainingPages=" + remainingPages +
				", servers=" + Arrays.stream(servers).map(Server::toString).collect(Collectors.joining("\n")) +
				'}';
	}
}
