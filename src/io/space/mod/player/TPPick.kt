package io.space.mod.player

import com.darkmagician6.eventapi.EventTarget
import io.space.events.EventPreUpdate
import io.space.mod.Mod
import io.space.value.values.NumberValue
import net.minecraft.entity.item.EntityItem

class TPPick : Mod("TPPick",Category.PLAYER) {
    private val range = NumberValue("Range",10.0,0.0,100.0,0.01)

    init {
        registerValues(range)
    }

    @EventTarget
    fun onPreUpdate(e : EventPreUpdate) {
        for (entity in mc.world.loadedEntityList) {
            if (entity is EntityItem) {
                if (mc.player.getDistanceToEntity(entity) <= range.value) {
                    if (entity.onGround) {
                        e.x = entity.posX
                        e.y = entity.posY
                        e.z = entity.posZ
                    }
                    break
                }
            }
        }
    }
}