package io.space.network.common.server;

import io.space.network.common.IPacket;
import io.space.network.common.IPacketHandler;
import io.space.network.common.ProxyByteBuf;

public class SPacketMessage implements IPacket {
    private String message;

    public SPacketMessage() {
    }

    public SPacketMessage(String message) {
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
        handler.toSPacketHandler().processMessage(this);
    }

    public String getMessage() {
        return message;
    }
}
