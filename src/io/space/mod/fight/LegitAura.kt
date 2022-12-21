package io.space.mod.fight

import com.darkmagician6.eventapi.EventTarget
import io.space.`object`.CPSDelay
import io.space.`object`.RotationObject
import io.space.events.EventPreUpdate
import io.space.events.EventTick
import io.space.mod.Mod
import io.space.mod.other.Teams
import io.space.utils.RotationUtils
import io.space.value.values.BooleanValue
import io.space.value.values.NumberValue
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.passive.EntityVillager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemFishingRod
import net.minecraft.item.ItemSword
import java.util.concurrent.ThreadLocalRandom

class LegitAura : Mod("LegitAura",Category.FIGHT) {
    private val searchRange = NumberValue("SearchRange", 3.0,0.0,8.0,0.01)
    private val blockDelay = NumberValue("BlockDelay", 2.0,1.0,10.0,1.0)

    private val maxCps: NumberValue = object : NumberValue("MaxCPS", 6.0,1.0,20.0,1.0) {
        override fun onChangeValue(preValue: Double, postValue: Double): Boolean {
            return postValue >= minCps.value
        }
    }
    private val minCps: NumberValue = object : NumberValue("MinCPS", 6.0,1.0,20.0,1.0) {
        override fun onChangeValue(preValue: Double, postValue: Double): Boolean {
            return postValue <= maxCps.value
        }
    }

    private val maxRotationSpeed : NumberValue = object : NumberValue("MaxRotationSpeed",40.0,1.0,180.0,1.0) {
        override fun onChangeValue(preValue: Double,postValue: Double): Boolean {
            return postValue >= minRotationSpeed.value
        }
    }

    private val minRotationSpeed : NumberValue = object : NumberValue("MinRotationSpeed",40.0,1.0,180.0,1.0) {
        override fun onChangeValue(preValue: Double,postValue: Double): Boolean {
            return postValue <= maxRotationSpeed.value
        }
    }

    private val autoBlock = BooleanValue("AutoBlock",false)
    private val batter = BooleanValue("Batter",false)
    private val player = BooleanValue("Player", true)
    private val monster = BooleanValue("Monster", false)
    private val animal = BooleanValue("Animal", false)
    private val villager = BooleanValue("Villager", false)
    private val invisibility = BooleanValue("Invisibility", false)

    private val cpsDelay = CPSDelay()
    private val rotationObject = RotationObject()

    private var target : EntityLivingBase? = null
    private var blocking = false
    private var fishingRodThrow = false
    private var fishingRodSwitchOld = 0

    init {
        registerValues(searchRange,blockDelay,maxCps,minCps,maxRotationSpeed,minRotationSpeed,autoBlock,batter,player,monster,animal,villager,invisibility)
    }

    @EventTarget
    fun onTick(e : EventTick) {
        target = null

        if (!autoBlock.value) {
            blocking = false
        }

        for (entity in mc.world.loadedEntityList) {
            if (shouldAddEntity(entity)) {
                target = entity as EntityLivingBase?
                break
            }
        }

        if (target != null) {
            val cps = if (minCps.value == maxCps.value) maxCps.value else ThreadLocalRandom.current().nextInt(minCps.value.toInt(),maxCps.value.toInt())

            if (mc.player.ticksExisted % blockDelay.value.toInt() == 0) {
                startBlock()
            } else stopBlock()

            if (cpsDelay.shouldAttack(cps.toInt())) {
                stopBlock()
                mc.clickMouse()

                if (batter.value) {
                    for (i in mc.player.inventory.mainInventory.indices) {
                        if (i > 9) break

                        val itemStack = mc.player.inventory.mainInventory[i]

                        if (itemStack?.item is ItemFishingRod) {
                            if (fishingRodThrow) {
                                mc.rightClickDelayTimer = 0
                                mc.rightClickMouse()
                                mc.player.inventory.currentItem = fishingRodSwitchOld
                                fishingRodThrow = false
                            } else {
                                fishingRodSwitchOld = mc.player.inventory.currentItem
                                mc.player.inventory.currentItem = i
                                mc.rightClickDelayTimer = 0
                                mc.rightClickMouse()
                                fishingRodThrow = true
                            }

                            break
                        }
                    }
                }
            }
        } else {
            rotationObject.yaw = mc.player.rotationYaw
            rotationObject.pitch = mc.player.rotationPitch
            stopBlock()
        }
    }

    private fun startBlock() {
        if (autoBlock.value && !blocking) {
            if (mc.player.heldItem?.item is ItemSword) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.keyCode, true)
                mc.playerController.sendUseItem(mc.player, mc.world, mc.player.inventory.getCurrentItem())
                blocking = true
            }
        }
    }

    private fun stopBlock() {
        if (autoBlock.value && blocking) {
            if (mc.player.heldItem?.item is ItemSword) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.keyCode, false)
                mc.playerController.onStoppedUsingItem(mc.player)
                mc.player.itemInUseCount = 0
                blocking = false
            }
        }
    }

    override fun onEnable() {
        super.onEnable()
        rotationObject.setYawPitch(mc.player.rotationYaw,mc.player.rotationPitch)
    }

    override fun onDisable() {
        super.onDisable()
        stopBlock()
    }

    @EventTarget
    fun onPreUpdate(e : EventPreUpdate) {
        if (target != null) {
            val rotation = RotationUtils.getPredictedRotations(target)

            if (maxRotationSpeed.value == 180.0 && minRotationSpeed.value == 180.0) {
                mc.player.rotationYaw = rotation[0]
                mc.player.rotationPitch = rotation[1]
            } else {
                val rotationSpeed = if (maxRotationSpeed.value == minRotationSpeed.value) maxRotationSpeed.value else ThreadLocalRandom.current().nextDouble(minRotationSpeed.value,maxRotationSpeed.value)

                rotationObject.rotationWithSpeed(rotation[0], rotation[1],rotationSpeed)

                mc.player.rotationYaw = rotationObject.yaw
                mc.player.rotationPitch = rotationObject.pitch
            }
        }
    }

    private fun shouldAddEntity(entity : Entity) : Boolean {
        if (entity == mc.player) return false
        if (entity !is EntityLivingBase) return false
        if (!entity.isEntityAlive) return false
        if (mc.player.getDistanceToEntity(entity) > searchRange.value) return false
        if (!invisibility.value && entity.isInvisible) return false
        if (Teams.isOnSameTeam(entity)) return false

        if (player.value && entity is EntityPlayer) {
            return true
        }

        if (monster.value && entity is EntityMob) {
            return true
        }

        if (animal.value && entity is EntityAnimal) {
            return true
        }

        if (villager.value && entity is EntityVillager) {
            return true
        }

        return false
    }
}