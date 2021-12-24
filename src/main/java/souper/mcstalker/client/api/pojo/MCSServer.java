package souper.mcstalker.client.api.pojo;

public class MCSServer
{
    private final long createdAt, updatedAt;
    private final int protocol, online, max;
    private final String country, ip;
    private final boolean alive, vanilla;

    public MCSServer(long createdAt, long updatedAt, int protocol, int online, int max, String country, boolean alive, boolean vanilla, String ip)
    {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.protocol = protocol;
        this.online = online;
        this.max = max;
        this.country = country;
        this.ip = ip;
        this.alive = alive;
        this.vanilla = vanilla;
    }
    public String getIP()
    {
        return this.ip;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public int getProtocol() {
        return protocol;
    }

    public int getOnline() {
        return online;
    }

    public int getMax() {
        return max;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return "MCSServer{" +
                "createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", protocol=" + protocol +
                ", online=" + online +
                ", max=" + max +
                ", country='" + country + '\'' +
                ", ip='" + ip + '\'' +
                ", alive=" + alive +
                ", vanilla=" + vanilla +
                '}';
    }
}
