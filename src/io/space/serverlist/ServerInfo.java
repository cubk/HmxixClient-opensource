package io.space.serverlist;

import net.minecraft.util.ResourceLocation;

public final class ServerInfo {
    private final ResourceLocation serverIcon;
    private final String serverName,serverIp,serverInfo;

    public ServerInfo(ResourceLocation serverIcon, String serverName, String serverIp, String serverInfo) {
        this.serverIcon = serverIcon;
        this.serverName = serverName;
        this.serverIp = serverIp;
        this.serverInfo = serverInfo;
    }

    public ResourceLocation getServerIcon() {
        return serverIcon;
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerIp() {
        return serverIp;
    }

    public String getServerInfo() {
        return serverInfo;
    }
}
