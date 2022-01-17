package com.mcstalker.networking.objects;

import com.mcstalker.utils.Skip;

public class FilterProperties implements Cloneable {

	private static FilterProperties INSTANCE = new FilterProperties();

	public static FilterProperties getInstance() {
		return INSTANCE;
	}

	public static void setInstance(FilterProperties instance) {
		INSTANCE = instance;
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

	@Override
	public FilterProperties clone() {
		try {
			return (FilterProperties) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}