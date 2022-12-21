package io.space.designer.designerimpl;

import io.space.Wrapper;
import io.space.designer.Designer;
import io.space.designer.GuiDesigner;
import io.space.mod.fight.KillAura;
import io.space.mod.other.Teams;
import io.space.mod.visual.Radar;
import io.space.mod.visual.TargetHUD;
import io.space.utils.RenderUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;

public final class TargetHUDDesigner extends Designer {
    public static TargetHUDDesigner Instance;

    public TargetHUDDesigner () {
        super(Type.TARGET_HUD);
        x = 2;
        y = 50;


        Instance = this;
    }

    @Override
    public void draw(float partialTicks, int mouseX, int mouseY) {
        final EntityLivingBase entity;

        if(mc.currentScreen instanceof GuiDesigner || mc.currentScreen instanceof GuiChat){
            entity = mc.player;
        }else {
            entity = KillAura.target;
        }

        if (entity != null) {
            if (TargetHUD.mode.isCurrentMode("Space")) {
                TargetHUD.renderSpace(new ScaledResolution(mc),entity);
            } else if (TargetHUD.mode.isCurrentMode("Minecraft")) {
                TargetHUD.renderMinecraft(new ScaledResolution(mc),entity);
            } else if (TargetHUD.mode.isCurrentMode("Astolfo")) {
                TargetHUD.renderAstolfo(new ScaledResolution(mc),entity);
            }
        }
    }

    @Override
    public boolean canDrag (int mouseX, int mouseY) {
        final double size = 100;
        return Wrapper.isHovered(x, y + 3, (x + size), (y + size), mouseX, mouseY) && Mouse.isButtonDown(0);
    }
}
