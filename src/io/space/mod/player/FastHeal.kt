package io.space.mod.player

import com.darkmagician6.eventapi.EventTarget
import io.space.Wrapper
import io.space.events.EventTick
import io.space.mod.Mod
import io.space.value.values.NumberValue
import net.minecraft.network.play.client.C03PacketPlayer

class FastHeal : Mod("FastHeal",Category.PLAYER) {
    private val packets = NumberValue("Packets",1.0,1.0,100.0,1.0)
    private val health = NumberValue("Health",10.0,0.0,100.0,0.5)

    init {
        registerValues(packets,health)
    }

    @EventTarget
    fun onTick(e : EventTick) {
        if (health.value > mc.player.health) {
            for (i in 1..packets.value.toInt()) {
                mc.netHandler.sendPacket(C03PacketPlayer(mc.player.onGround))
            }
        }
    }
}