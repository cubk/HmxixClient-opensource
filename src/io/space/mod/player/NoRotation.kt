package io.space.mod.player

import com.darkmagician6.eventapi.EventTarget
import io.space.events.EventPacket
import io.space.mod.Mod
import net.minecraft.network.play.server.S08PacketPlayerPosLook

class NoRotation : Mod("NoRotation",Category.PLAYER) {
    @EventTarget
    fun onPacket(e : EventPacket) {
        if (e.packet is S08PacketPlayerPosLook) {
            val packet = e.packet as S08PacketPlayerPosLook

            packet.yaw = mc.player.rotationYaw
            packet.pitch = mc.player.rotationPitch
        }
    }
}