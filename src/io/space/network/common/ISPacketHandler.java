package io.space.network.common;

import io.space.network.common.server.*;

public interface ISPacketHandler extends IPacketHandler {
    void processAuthResult(SPacketAuthResult packet);
    void processRegisterResult(SPacketRegisterResult packet);
    void processMessage(SPacketMessage packet);
    void processServerClose(SPacketServerClose packet);
    void processKick(SPacketKick packet);
}
