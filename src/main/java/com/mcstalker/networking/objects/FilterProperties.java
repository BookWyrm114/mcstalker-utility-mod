package com.mcstalker.networking.objects;

public class FilterProperties {
	public static Filters.SortMode sortMode = Filters.SortMode.UPDATED;
	public static Filters.AscDesc ascdesc = Filters.AscDesc.DESC;
	public static Filters.MinecraftVersion version = new Filters.MinecraftVersion(-1, "All");
	public static Filters.Country country = Filters.Country.ALL;
	public static boolean mustHavePeople = false;
	public static boolean vanillaOnly = false;
	public static String searchText = "";
	public static int page = 1;
	public static Filters.WhiteListStatus whiteListStatus = Filters.WhiteListStatus.ANY;
	public static Filters.AuthStatus authStatus = Filters.AuthStatus.ANY;
}