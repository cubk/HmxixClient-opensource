package io.space.mod.fight;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPreUpdate;
import io.space.mod.Mod;
import io.space.utils.InventoryUtils;
import io.space.value.values.BooleanValue;
import io.space.value.values.NumberValue;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.PotionEffect;
import utils.hodgepodge.object.time.TimerUtils;

public final class AutoPot extends Mod {
	public final BooleanValue speed = new BooleanValue("SpeedPotion",true);
	public final BooleanValue regen = new BooleanValue("RegenPotion",true);
	public final NumberValue health = new NumberValue("Health",6,0.5,20.0,0.5);
	public final BooleanValue predict = new BooleanValue("Predict",false);

    public boolean potting;
    private final TimerUtils timerUtils = new TimerUtils(false);

    public AutoPot() {
        super("AutoPot",Category.FIGHT);
        registerValues(speed, regen, health, predict);
    }

    @EventTarget
    public void onPre(EventPreUpdate e) {
        if (timerUtils.hasReached(200)) {
            if (potting)
                potting = false;
        }
        int spoofSlot = getBestSpoofSlot();
        int[] pots = {6, -1, -1};

        if (regen.getValue())
            pots[1] = 10;
        if (speed.getValue())
            pots[2] = 1;

        for (int pot : pots) {
            if (pot == -1)
                continue;
            if (pot == 6 || pot == 10) {
                if (timerUtils.hasReached(900) && !mc.player.isPotionActive(pot)) {
                    if (mc.player.getHealth() < (health.getValue())) {
                        getBestPot(spoofSlot, pot);
                    }
                }
            } else if (timerUtils.hasReached(1000) && !mc.player.isPotionActive(pot)) {
                getBestPot(spoofSlot, pot);
            }
        }
    }

    int getBestSpoofSlot(){  	
    	int spoofSlot = 5;
    	for (int i = 36; i < 45; i++) {       		
    		if (!mc.player.inventoryContainer.getSlot(i).getHasStack()) {
     			spoofSlot = i - 36;
     			break;
            }else if(mc.player.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemPotion) {
            	spoofSlot = i - 36;
     			break;
            }
        }
    	return spoofSlot;
    }

    void getBestPot(int hotbarSlot, int potID){
    	for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack() &&(mc.currentScreen == null || mc.currentScreen instanceof GuiInventory)) {
                final ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                if(is.getItem() instanceof ItemPotion){
              	  final ItemPotion pot = (ItemPotion)is.getItem();
              	  if(pot.getEffects(is).isEmpty())
              		  return;
              	  final PotionEffect effect = pot.getEffects(is).get(0);
                  final int potionID = effect.getPotionID();
                  if(potionID == potID)
              	  if(ItemPotion.isSplash(is.getItemDamage()) && isBestPot(pot, is)){
              		  if(36 + hotbarSlot != i)
              			  InventoryUtils.swap(i, hotbarSlot);
              		  timerUtils.reset();
              		  final int oldSlot = mc.player.inventory.currentItem;
              		  mc.getNetHandler().sendPacket(new C09PacketHeldItemChange(hotbarSlot));
          			  mc.getNetHandler().sendPacket(new C03PacketPlayer.C05PacketPlayerLook(mc.player.rotationYaw,85, mc.player.onGround));
          			  mc.getNetHandler().sendPacket(new C08PacketPlayerBlockPlacement(mc.player.inventory.getCurrentItem()));
          			  mc.getNetHandler().sendPacket(new C09PacketHeldItemChange(oldSlot));
          			  potting = true;
          			  break;
              	  }               	  
                }              
            }
        }
    }
    
    boolean isBestPot(ItemPotion potion, ItemStack stack){
    	if(potion.getEffects(stack) == null || potion.getEffects(stack).size() != 1)
    		return false;
        final PotionEffect effect = potion.getEffects(stack).get(0);
        final int potionID = effect.getPotionID();
        final int amplifier = effect.getAmplifier();
        final int duration = effect.getDuration();
    	for (int i = 9; i < 45; i++) {    		
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {           	
                final ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                if(is.getItem() instanceof ItemPotion){
                	ItemPotion pot = (ItemPotion)is.getItem();
                	 if (pot.getEffects(is) != null) {
                         for (PotionEffect o : pot.getEffects(is)) {
                             final int id = o.getPotionID();
                             final int ampl = o.getAmplifier();
                             final int dur = o.getDuration();
                             if (id == potionID && ItemPotion.isSplash(is.getItemDamage())){
                            	 if(ampl > amplifier){
                            		 return false;
                            	 } else if (ampl == amplifier && dur > duration){
                            		 return false;
                            	 }
                             }                            
                         }
                     }
                }
            }
        }
    	return true;
    }
}
