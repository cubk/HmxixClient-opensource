package io.space.mod.other;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.Event2D;
import io.space.events.EventPacket;
import io.space.mod.Mod;
import io.space.utils.RenderUtils;
import io.space.value.values.BooleanValue;
import net.minecraft.network.Packet;
import utils.hodgepodge.object.StringUtils;

import java.util.LinkedList;

public final class PacketMonitor extends Mod {
    private final BooleanValue clientPacket = new BooleanValue("ClientPacket",true);
    private final BooleanValue serverPacket = new BooleanValue("ServerPacket",true);

    private final LinkedList<PacketObject> clientPackets = new LinkedList<>();
    private final LinkedList<PacketObject> serverPackets = new LinkedList<>();

    public PacketMonitor() {
        super("PacketMonitor",Category.OTHER);
        registerValues(clientPacket,serverPacket);
    }

    @Override
    protected void onDisable() {
        clientPackets.clear();
        serverPackets.clear();
        super.onDisable();
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if (clientPacket.getValue() && e.isSend()) {
            labelClientPacket:
            {
                for (PacketObject packet : clientPackets) {
                    if (packet.getName().equals(e.getPacket().getClass().getSimpleName())) {
                        packet.increase();
                        break labelClientPacket;
                    }
                }
                clientPackets.add(new PacketObject(e.getPacket()));
            }
        }

        if (serverPacket.getValue() && !e.isSend()) {
            labelServerPacket:
            {
                for (PacketObject packet : serverPackets) {
                    if (packet.getName().equals(e.getPacket().getClass().getSimpleName())) {
                        packet.increase();
                        break labelServerPacket;
                    }
                }
                serverPackets.add(new PacketObject(e.getPacket()));
            }
        }
    }

    @EventTarget
    public void on2D(Event2D e) {
        if (clientPacket.getValue()) {
            clientPackets.sort((o1, o2) -> o2.getQuantity() - o1.getQuantity());
            float clientPacketTextY = 103;
            for (PacketObject packet : clientPackets) {
                 mc.unicodeFontRenderer.drawStringWithShadow(StringUtils.buildString(packet.getName()," ",packet.getQuantity()), 4, clientPacketTextY, RenderUtils.getRGB(255,0,0));
                clientPacketTextY += 8;
            }
        }
        if (serverPacket.getValue()) {
            serverPackets.sort((o1, o2) -> o2.getQuantity() - o1.getQuantity());
            float serverPacketTextY = 103;
            for (PacketObject packet : serverPackets) {
                mc.unicodeFontRenderer.drawStringWithShadow(StringUtils.buildString(packet.getName()," ",packet.getQuantity()), 180, serverPacketTextY, RenderUtils.getRGB(255,0,0));
                serverPacketTextY += 8;
            }
        }
    }

    private static class PacketObject {
        private final String name;
        private int quantity;
        private final Packet<?> packet;

        public PacketObject(Packet<?> packet) {
            this.name = packet.getClass().getSimpleName();
            this.quantity = 1;
            this.packet = packet;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        public void increase() {
            quantity++;
        }

        public Packet<?> getPacket() {
            return packet;
        }
    }
}
