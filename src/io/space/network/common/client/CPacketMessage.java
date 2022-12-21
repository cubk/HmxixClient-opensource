package io.space.network.common.client;

import io.space.network.common.IPacket;
import io.space.network.common.IPacketHandler;
import io.space.network.common.ProxyByteBuf;

public class CPacketMessage implements IPacket {
    private String message;

    public CPacketMessage() {
    }

    public CPacketMessage(String message) {
        this.message = message;
    }

    @Override
    public void read(ProxyByteBuf buf) {
        message = buf.readString();
    }

    @Override
    public void write(ProxyByteBuf buf) {
        buf.writeString(message);
    }

    @Override
    public void process(IPacketHandler handler) {
        handler.toCPacketHandler().processMessage(this);
    }

    public String getMessage() {
        return message;
    }
}
