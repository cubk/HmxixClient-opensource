package io.space.network.common.client;

import io.space.network.common.IPacket;
import io.space.network.common.IPacketHandler;
import io.space.network.common.ProxyByteBuf;

public class CPacketAuth implements IPacket {
    private String name;
    private String password;
    private String hardwareID;

    public CPacketAuth() {
    }

    public CPacketAuth(String name, String password, String hardwareID) {
        this.name = name;
        this.password = password;
        this.hardwareID = hardwareID;
    }

    @Override
    public void read(ProxyByteBuf buf) {
        name = buf.readString();
        password = buf.readString();
        hardwareID = buf.readString();
    }

    @Override
    public void write(ProxyByteBuf buf) {
        buf.writeString(name);
        buf.writeString(password);
        buf.writeString(hardwareID);
    }

    @Override
    public void process(IPacketHandler handler) {
        handler.toCPacketHandler().processAuth(this);
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getHardwareID() {
        return hardwareID;
    }
}
