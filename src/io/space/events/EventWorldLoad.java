package io.space.events;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.client.multiplayer.WorldClient;

public final class EventWorldLoad implements Event {
    private final WorldClient worldClient;
    private final String loadingMessage;

    public EventWorldLoad(WorldClient worldClient, String loadingMessage) {
        this.worldClient = worldClient;
        this.loadingMessage = loadingMessage;
    }

    public WorldClient getWorldClient() {
        return worldClient;
    }

    public String getLoadingMessage() {
        return loadingMessage;
    }
}
