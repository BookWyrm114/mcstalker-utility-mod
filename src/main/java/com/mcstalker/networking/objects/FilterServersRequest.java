package com.mcstalker.networking.objects;

/**
 * https://github.com/MC-Stalker/API/wiki/FilterServers
 */
public record FilterServersRequest(
	Filters.SortMode sortMode,
	Filters.AscDesc ascDesc,
	Filters.MinecraftVersion version,
	Filters.Country country,
	boolean mustHavePeople,
	boolean vanillaOnly,
	String searchText,
	int page,
	Filters.WhiteListStatus whiteListStatus,
	Filters.AuthStatus authStatus
) {}