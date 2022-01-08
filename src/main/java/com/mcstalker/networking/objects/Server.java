package com.mcstalker.networking.objects;

public record Server(
    long createdAt,
	long updatedAt,
	long lastSeen,
	String ip,
	String versionName,
	int protocol,
	int online,
	int max,
	String favicon,
	Object ipInfo,
	Object motd,
	int authStatus,
	String searchMotd,
	boolean alive,
	boolean vanilla,
	boolean modded
) {
	@Override
	public String toString() {
		return "Server{" +
				"createdAt=" + createdAt +
				", updatedAt=" + updatedAt +
				", lastSeen=" + lastSeen +
				", ip='" + ip + '\'' +
				", versionName='" + versionName + '\'' +
				", protocol=" + protocol +
				", online=" + online +
				", max=" + max +
				", favicon=..." +
				", ipInfo=" + ipInfo +
				", motd=" + motd +
				", authStatus=" + authStatus +
				", searchMotd='" + searchMotd + '\'' +
				", alive=" + alive +
				", vanilla=" + vanilla +
				", modded=" + modded +
				'}';
	}
}