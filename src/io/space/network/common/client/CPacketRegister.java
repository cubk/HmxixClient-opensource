package io.space.network.common.client;

import io.space.network.common.IPacket;
import io.space.network.common.IPacketHandler;
import io.space.network.common.ProxyByteBuf;

public class CPacketRegister implements IPacket {
    private String name;
    private String password;
    private String hardwareID;
    private String activationCode;

    public CPacketRegister() {
    }

    public CPacketRegister(String name, String password, String hardwareID, String activationCode) {
        this.name = name;
        this.password = password;
        this.hardwareID = hardwareID;
        this.activationCode = activationCode;
    }

    @Override
    public void read(ProxyByteBuf buf) {
        name = buf.readString();
        password = buf.readString();
        hardwareID = buf.readString();
        activationCode = buf.readString();
    }

    @Override
    public void write(ProxyByteBuf buf) {
        buf.writeString(name);
        buf.writeString(password);
        buf.writeString(hardwareID);
        buf.writeString(activationCode);
    }

    @Override
    public void process(IPacketHandler handler) {
        handler.toCPacketHandler().processRegister(this);
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

    public String getActivationCode() {
        return activationCode;
    }
}
