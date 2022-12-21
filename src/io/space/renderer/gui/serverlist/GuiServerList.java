package io.space.renderer.gui.serverlist;

import io.space.renderer.font.FontManager;
import io.space.serverlist.ServerInfo;
import io.space.serverlist.ServerListManager;
import io.space.utils.GLUtils;
import io.space.utils.RenderUtils;
import io.space.object.SlidingCalculation;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class GuiServerList extends GuiScreen {
    private static final GLUtils GL = new GLUtils();

    private final SlidingCalculation slidingCalculation = new SlidingCalculation(50,50);

    private double animationY;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        animationY = RenderUtils.getAnimationStateEasing(animationY,slidingCalculation.getCurrent(),6);

        RenderUtils.drawRect(2,2,265,height - 2,RenderUtils.getRGB(0,0,0,140));

        double infoY = 4 - animationY;
        int rendered = 0;

        RenderUtils.startGlScissor(2,2,258,height - 2);

        for (ServerInfo serverInfo : ServerListManager.Instance.getServerMap().values()) {
            RenderUtils.drawImage(4,infoY,48,48,-1,serverInfo.getServerIcon());

            final double finalInfoY = infoY;

            GLUtils.pushScale(2.0,2.0,2.0,() -> mc.customFontRenderer.drawString(serverInfo.getServerName(),58.0f / 2.0f,((float) finalInfoY) / 2.0f,-1));

            FontManager.default16.drawStringWithOutline(serverInfo.getServerInfo(),58.0,infoY + 20,-1);
            FontManager.default16.drawStringWithOutline(serverInfo.getServerIp(),58.0,infoY + 32,-1);

            if (infoY > 0) {
                rendered++;
            }

            infoY += 58;
        }

        RenderUtils.stopGlScissor();

        if (slidingCalculation.getCurrent() < 0.0) {
            slidingCalculation.setCurrent(0.0);
        }

        if (slidingCalculation.getCurrent() > (rendered * 58 * 2)) {
            slidingCalculation.setCurrent((rendered * 58 * 2) + 58);
        }

        slidingCalculation.calculation();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        switch (button.id) {
            case 0:
                mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
                break;
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        buttonList.add(new GuiButton(0,width - 62,height - 22,58,20,"Back"));
    }
}
