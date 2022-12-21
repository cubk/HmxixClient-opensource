package io.space.global;

import io.space.value.values.BooleanValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import utils.hodgepodge.io.IOUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

public final class GlobalSetting {
    public static GlobalSetting Instance;

    private final LinkedHashMap<String,BooleanValue> Values = new LinkedHashMap<>();
    private final BooleanValue TranslateModName = new BooleanValue("TranslateModName",false);
    private final BooleanValue Cape = new BooleanValue("ClientCape",false);
    private final BooleanValue NoCommand = new BooleanValue("NoCommand",false);
    private final BooleanValue FakeForge = new BooleanValue("FakeForge",false);

    private DynamicTexture Background;

    public GlobalSetting() {
        registerSettings(TranslateModName,Cape,NoCommand,FakeForge);

        final File file = new File(new File(Minecraft.getMinecraft().mcDataDir,"space/"),"background.bin");

        try {
            if (file.exists()) {
                Background = new DynamicTexture(ImageIO.read(new FileInputStream(file)));
            } else {
                Background = new DynamicTexture(ImageIO.read(IOUtils.getResourceAsStream("assets/space/background.png")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerSettings(BooleanValue... booleanValues) {
        for (BooleanValue booleanValue : booleanValues) Values.put(booleanValue.getValueName(),booleanValue);
    }

    public LinkedHashMap<String,BooleanValue> getValues() {
        return Values;
    }

    public BooleanValue getTranslateModName() {
        return TranslateModName;
    }

    public BooleanValue getCape() {
        return Cape;
    }

    public BooleanValue getNoCommand() {
        return NoCommand;
    }

    public BooleanValue getFakeForge() {
        return FakeForge;
    }

    public DynamicTexture getBackground() {
        return Background;
    }
}
