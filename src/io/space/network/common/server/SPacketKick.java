package io.space.network.common.server;

import io.space.network.common.IPacket;
import io.space.network.common.IPacketHandler;
import io.space.network.common.ProxyByteBuf;

public class SPacketKick implements IPacket {
    @Override
    public void read(ProxyByteBuf buf) {

    }

    @Override
    public void write(ProxyByteBuf buf) {

    }

    @Override
    public void process(IPacketHandler handler) {
        handler.toSPacketHandler().processKick(this);
    }
}
