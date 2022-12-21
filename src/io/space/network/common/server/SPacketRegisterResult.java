package io.space.network.common.server;

import io.space.network.common.IPacket;
import io.space.network.common.IPacketHandler;
import io.space.network.common.ProxyByteBuf;

public class SPacketRegisterResult implements IPacket {
    private String result;

    public SPacketRegisterResult() {
    }

    public SPacketRegisterResult(String result) {
        this.result = result;
    }

    @Override
    public void read(ProxyByteBuf buf) {
        result = buf.readString();
    }

    @Override
    public void write(ProxyByteBuf buf) {
        buf.writeString(result);
    }

    @Override
    public void process(IPacketHandler handler) {
        handler.toSPacketHandler().processRegisterResult(this);
    }

    public String getResult() {
        return result;
    }
}
