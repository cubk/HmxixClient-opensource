package io.space.value.values;

import io.space.value.Value;
import io.space.value.ValueType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

public class TextValue extends Value<String> {
    private final GuiTextField guiTextField;

    public TextValue(String valueName,String defaultValue) {
        super(valueName, ValueType.TEXT_VALUE);
        setValueDirect(defaultValue);

        this.guiTextField = new GuiTextField(0,Minecraft.getMinecraft().fontRenderer,0,0,140,10);
        this.guiTextField.setText(defaultValue);
    }

    public GuiTextField getGuiTextField() {
        return guiTextField;
    }
}
