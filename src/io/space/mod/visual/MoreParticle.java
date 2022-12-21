package io.space.mod.visual;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPacket;
import io.space.mod.Mod;
import io.space.value.values.BooleanValue;
import io.space.value.values.NumberValue;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.EnumParticleTypes;

public final class MoreParticle extends Mod {
    private final NumberValue crackSize = new NumberValue("CrackSize",2.0, 0.0, 10.0, 1.0);
    private final BooleanValue critical = new BooleanValue("Critical",true);
    private final BooleanValue normal = new BooleanValue("Normal",false);

    public MoreParticle() {
        super("MoreParticle", Category.VISUAL);
        registerValues(crackSize,critical,normal);
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        C02PacketUseEntity packet;
        if (e.getPacket() instanceof C02PacketUseEntity && (packet = (C02PacketUseEntity)e.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK) {
            for (int index = 0; index < crackSize.getValue().intValue(); ++index) {
                if (critical.getValue()) {
                    mc.effectRenderer.emitParticleAtEntity(packet.getEntityFromWorld(MoreParticle.mc.world), EnumParticleTypes.CRIT);
                }

                if (normal.getValue()) {
                    mc.effectRenderer.emitParticleAtEntity(packet.getEntityFromWorld(MoreParticle.mc.world), EnumParticleTypes.CRIT_MAGIC);
                }
            }
        }
    }
}
