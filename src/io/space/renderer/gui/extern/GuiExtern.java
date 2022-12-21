package io.space.renderer.gui.extern;

import net.minecraft.client.gui.GuiScreen;

public class GuiExtern extends GuiScreen {
    private final GuiScreen parentScreen;

    public GuiExtern(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
