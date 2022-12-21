package io.space.network.common;

public interface IPacketHandler {
    default ICPacketHandler toCPacketHandler() {
        return (ICPacketHandler) this;
    }

    default ISPacketHandler toSPacketHandler() {
        return (ISPacketHandler) this;
    }
}
