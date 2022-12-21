package io.space.network.common.server;

import io.space.network.common.IPacket;
import io.space.network.common.IPacketHandler;
import io.space.network.common.ProxyByteBuf;

public class SPacketAuthResult implements IPacket {
    private int result;
    private String reason;

    public SPacketAuthResult() {
    }

    public SPacketAuthResult(int result, String reason) {
        this.result = result;
        this.reason = reason;
    }

    @Override
    public void read(ProxyByteBuf buf) {
        result = buf.readInt();
        reason = buf.readString();
    }

    @Override
    public void write(ProxyByteBuf buf) {
        buf.writeInt(result);
        buf.writeString(reason);
    }

    @Override
    public void process(IPacketHandler handler) {
        handler.toSPacketHandler().processAuthResult(this);
    }

    public int getResult() {
        return result;
    }

    public String getReason() {
        return reason;
    }
}
