package io.space.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import net.minecraft.network.Packet;

public final class EventPacket extends EventCancellable {
    private final boolean isSend;
    private final Packet<?> packet;

    public EventPacket(Packet<?> packet,boolean isSend) {
        this.packet = packet;
        this.isSend = isSend;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public boolean isSend() {
        return isSend;
    }
}
