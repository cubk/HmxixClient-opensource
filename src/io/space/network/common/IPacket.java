package io.space.network.common;

public interface IPacket {
    void read(ProxyByteBuf buf);

    void write(ProxyByteBuf buf);

    void process(IPacketHandler handler);
}
