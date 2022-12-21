package io.space.designer;

import io.space.utils.RenderUtils;
import net.minecraft.client.gui.GuiScreen;

public final class
GuiDesigner extends GuiScreen {
    private static Designer selectDesigner;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtils.drawRect(0,0,width,height,RenderUtils.getRGB(0,0,0,100));

        for (Designer designer : DesignerManager.Instance.getDesigners()) {
            if (designer.canDrag(mouseX, mouseY)) {
                selectDesigner = designer;
            }

            designer.draw(partialTicks,mouseX,mouseY);
        }

        if (selectDesigner != null) {
            if (selectDesigner.canDrag(mouseX, mouseY)) {
                selectDesigner.doDrag(mouseX,mouseY);
            } else selectDesigner.resetDrag();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
