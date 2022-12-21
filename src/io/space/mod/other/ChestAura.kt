package io.space.mod.other

import com.darkmagician6.eventapi.EventTarget
import io.space.events.Event3D
import io.space.events.EventPostUpdate
import io.space.events.EventPreUpdate
import io.space.events.EventWorldLoad
import io.space.mod.Mod
import io.space.utils.PositionUtils
import io.space.utils.RenderUtils
import io.space.utils.RotationUtils
import io.space.value.values.BooleanValue
import io.space.value.values.NumberValue
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.tileentity.TileEntityChest
import net.minecraft.util.EnumFacing

class ChestAura : Mod("ChestAura",Category.OTHER) {
    private val range = NumberValue("Range",4.0,0.0,8.0,0.01)
    private val rotation = BooleanValue("Rotation",true)

    private val openedChests = ArrayList<TileEntityChest>(256)

    private var target : TileEntityChest? = null

    init {
        registerValues(range,rotation)
    }

    @EventTarget
    fun onPreUpdate(e : EventPreUpdate) {
        if (openedChests.size > 128) openedChests.clear()

        if (mc.currentScreen !is GuiChest) {
            target = null

            for (tileEntity in mc.world.loadedTileEntityList) {
                if (tileEntity is TileEntityChest && !openedChests.contains(tileEntity) && mc.player.getDistance(
                        tileEntity.pos.x.toDouble(),
                        tileEntity.pos.y.toDouble(),
                        tileEntity.pos.z.toDouble()
                    ) <= range.value
                ) {
                    openedChests.add(tileEntity)

                    if (rotation.value) {
                        val rotations = RotationUtils.getBlockPosRotation(tileEntity.pos)

                        e.setVisualYaw(rotations[0])
                        e.setVisualPitch(rotations[1])
                    }

                    target = tileEntity

                    break
                }
            }
        }
    }

    @EventTarget
    fun onWorldLoad(e : EventWorldLoad) {
        openedChests.clear()
    }

    @EventTarget
    fun onPostUpdate(e : EventPostUpdate) {
        if (mc.currentScreen !is GuiChest) {
            if (target != null) {
                if (mc.playerController.onPlayerRightClick(
                        mc.player,
                        mc.world,
                        mc.player.heldItem,
                        target?.pos,
                        EnumFacing.UP,
                        PositionUtils.getVec3(target?.pos, EnumFacing.UP)
                    )
                ) {
                    mc.player.swingItem()
                }
            }
        }
    }

    @EventTarget
    fun on3D(e : Event3D) {
        if (target != null) {
            RenderUtils.drawBlockBox(target?.pos,RenderUtils.getRGB(0,50,200,150),true)
        }
    }

    override fun onDisable() {
        super.onDisable()

        openedChests.clear()
    }

    override fun onEnable() {
        super.onEnable()

        openedChests.clear()
    }
}