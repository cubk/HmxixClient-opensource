package io.space.designer.designerimpl;

import io.space.Wrapper;
import io.space.designer.Designer;
import io.space.mod.other.Teams;
import io.space.mod.visual.Radar;
import io.space.utils.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;

public final class RadarDesigner extends Designer {
    public static RadarDesigner Instance;

    public RadarDesigner() {
        super(Type.RADAR);
        x = 2;
        y = 50;

        Instance = this;
    }

    @Override
    public void draw(float partialTicks, int mouseX, int mouseY) {
        final int size = Radar.size.getValue().intValue();
        RenderUtils.drawRect(x, y + 3, (x + size), (y + size),RenderUtils.getRGB(0, 0, 0, 50));
        RenderUtils.drawRect(x + ((size / 2.0) - 0.5), y + 3.5, x + (size / 2.0) + 0.5, (y + size),RenderUtils.getRGB(255,255,255,80));
        RenderUtils.drawRect(x, y + ((size / 2.0) - 0.5), (x + size), y + (size / 2.0) + 0.5, RenderUtils.getRGB(255,255,255,80));
        RenderUtils.drawRect(x, y + 2.5, (x + (size)), y + 3.6,RenderUtils.getRGB(0x9B9BFF));

        for (EntityPlayer playerEntity : mc.world.playerEntities) {
            if (playerEntity.isEntityAlive() && playerEntity != mc.player) {
                double posX = ((playerEntity.posX + (playerEntity.posX - playerEntity.lastTickPosX) * partialTicks - mc.player.posX) * Radar.scale.getValue());
                double posZ = ((playerEntity.posZ + (playerEntity.posZ - playerEntity.lastTickPosZ) * partialTicks - mc.player.posZ) * Radar.scale.getValue());
                int color = mc.player.canEntityBeSeen(playerEntity) ? RenderUtils.getRGB(255, 255, 254) : RenderUtils.getRGB(120, 120, 120);

                if (Teams.isOnSameTeam(playerEntity)) {
                    color = RenderUtils.getRGB(0, 255, 0);
                }

                if (playerEntity.hurtTime != 0) {
                    color = RenderUtils.getRGB(0, 255, 0);
                }

                draw(null,posX,posZ,color);
            }
        }

        if (Radar.pvp.getValue()) {
            draw("PVP",-mc.player.posX * Radar.scale.getValue(),-mc.player.posZ * Radar.scale.getValue(),RenderUtils.getRGB(20, 140, 220));
        }
    }

    private void draw(String str,double posX,double posZ,int color) {
        final int size = Radar.size.getValue().intValue();
        double cos = Math.cos(mc.player.rotationYaw * (Math.PI / 180.0));
        double sin = Math.sin(mc.player.rotationYaw * (Math.PI / 180.0));
        double rotY = -(posZ * cos - posX * sin);
        double rotX = -(posX * cos + posZ * sin);

        if (rotY > (size / 2.0 - 1.5)) {
            rotY = (size / 2.0) - 1.5;
        } else if (rotY < (-(size / 2.0 - 5))) {
            rotY = (-(size / 2.0 - 5));
        }

        if (rotX > (size / 2.0) - 1.5) {
            rotX = (size / 2.0 - 1.5);
        } else if (rotX < (-(size / 2.0 - 1.5))) {
            rotX = -((size / 2.0) - 1.5);
        }

        RenderUtils.drawBorderedRect(((x + (size / 2.0) + rotX) - 1.5), ((y + (size / 2.0) + rotY) - 1.5), ((x + ((size / 2.0) + rotX) + 1.5)), ((y + (size / 2.0) + rotY) + 1.5), 0.5, RenderUtils.getRGB(16,16,16),color);

        if (str != null) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5, 0.5, 0.5);
            mc.customFontRenderer.drawStringWithOutline(str,(float) ((x + (size / 2.0) + rotX) - (mc.customFontRenderer.getStringWidth(str) / 4.0f)) * 2, (float) ((y + (size / 2.0) + rotY) + 2.5f) * 2,color);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean canDrag (int mouseX, int mouseY) {
        final double size = Radar.size.getValue();
        return Wrapper.isHovered(x, y + 3, (x + size), (y + size), mouseX, mouseY) && Mouse.isButtonDown(0);
    }
}
