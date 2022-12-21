package io.space.network.common;

import io.space.network.common.client.*;

public interface ICPacketHandler extends IPacketHandler {
    void processMessage(CPacketMessage packet);
    void processAuth(CPacketAuth packet);
    void processRegister(CPacketRegister packet);
}
