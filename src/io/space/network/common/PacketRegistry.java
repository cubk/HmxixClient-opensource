package io.space.network.common;

import io.space.network.common.client.*;
import io.space.network.common.server.*;

import java.util.HashMap;

public class PacketRegistry {
    private static final HashMap<Class<? extends IPacket>, Integer> ID_MAP = new HashMap<>();
    private static final HashMap<Integer, Class<? extends IPacket>> PACKET_CLASS_MAP = new HashMap<>();

    private static int count;

    public static void init() {
        register(CPacketMessage.class);
        register(CPacketAuth.class);
        register(CPacketRegister.class);
        count++; // CPacketRemoteChangeHardwareID

        register(SPacketAuthResult.class);
        register(SPacketRegisterResult.class);
        register(SPacketMessage.class);
        register(SPacketServerClose.class);
        register(SPacketKick.class);
        count++; // SPacketRemoteChangeHardwareIDResult
    }

    private static void register(Class<? extends IPacket> packetClass) {
        ID_MAP.put(packetClass, count);
        PACKET_CLASS_MAP.put(count, packetClass);

        count++;
    }

    public static int getID(Class<? extends IPacket> packetClass) {
        final Integer integer = ID_MAP.get(packetClass);

        if (integer == null) {
            return -1;
        }

        return integer;
    }

    public static Class<? extends IPacket> getPacketClass(int id) {
        return PACKET_CLASS_MAP.get(id);
    }
}
