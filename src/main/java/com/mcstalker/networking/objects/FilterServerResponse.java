package com.mcstalker.networking.objects;

import com.mcstalker.MCStalker;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FilterServerResponse {

	public final int page;
	public final int perPage;
	public final int remainingPages;
	public final Server[] servers;

	public FilterServerResponse(int page, int perPage, int remainingPages, Server[] servers) {
		this.page = page;
		this.perPage = perPage;
		this.remainingPages = remainingPages;
		this.servers = servers;
	}

	public ServerList getServerList() {
		MCStalkerServerList result = new MCStalkerServerList(MCStalker.MC);
		for (Server server : servers) {
			result.add(new ServerInfo(server.ip(), server.ip(), false));
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
