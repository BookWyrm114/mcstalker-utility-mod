package com.mcstalker.networking.objects;

import com.mcstalker.utils.Skip;

public class FilterProperties {

	private static FilterProperties INSTANCE;

	public static FilterProperties getInstance() {
		return INSTANCE;
	}

	public FilterProperties() {
		INSTANCE = this;
	}

	public Filters.SortMode sortMode = Filters.SortMode.UPDATED;
	public Filters.AscDesc ascdesc = Filters.AscDesc.DESC;
	public Filters.MinecraftVersion version = new Filters.MinecraftVersion(-1, "All");
	public Filters.Country country = Filters.Country.ALL;
	public boolean mustHavePeople = false;
	public boolean vanillaOnly = false;
	public String searchText = "";
	@Skip(deserialization = true, serialization = true)
	public int page = 1;
	public Filters.WhiteListStatus whiteListStatus = Filters.WhiteListStatus.ANY;
	public Filters.AuthStatus authStatus = Filters.AuthStatus.ANY;
}