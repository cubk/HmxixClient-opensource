package io.space.mod.player

import com.darkmagician6.eventapi.EventTarget
import io.space.events.EventTick
import io.space.mod.Mod

class AutoRespawn : Mod("AutoResapwn",Category.PLAYER) {
    @EventTarget
    fun onTick(e : EventTick) {
        if (!mc.player.isEntityAlive) mc.player.respawnPlayer()
    }
}