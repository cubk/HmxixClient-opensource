package io.space.translate;

import io.space.Wrapper;
import io.space.mod.Mod;
import io.space.mod.ModManager;
import utils.hodgepodge.io.FileUtils;
import utils.hodgepodge.object.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class TranslateManager {
    public static TranslateManager Instance;

    private final File TRANSLATE_FILE = new File(Wrapper.Instance.getClientDirectory(),"translate.str");
    private final File UNTRANSLATED_FILE = new File(Wrapper.Instance.getClientDirectory(),"未翻译translate.str");

    public void writeModTranslateFile() throws IOException {
        final StringBuilder stringBuilder = new StringBuilder("//请在更改完成之后将此文件重命名为\"translate.str\"");

        stringBuilder.append(System.lineSeparator()).append("//格式为 Mod原名=Mod翻译名").append(System.lineSeparator());

        for (Mod mod : ModManager.Instance.getModMap().values()) {
            stringBuilder.append(mod.getModName()).append("=").append(System.lineSeparator());
        }

        FileUtils.writeStringToFile(UNTRANSLATED_FILE,stringBuilder.toString(),StandardCharsets.UTF_8);
    }

    public void readTranslatedFile() throws IOException {
        if (TRANSLATE_FILE.exists()) {
            for (String s : FileUtils.readFileAsStringList(TRANSLATE_FILE, StandardCharsets.UTF_8)) {
                if (StringUtils.isNullOrEmpty(s)) continue;
                if (s.trim().startsWith("//")) continue;

                final String[] split = s.split("=");
                final String modName = split[0];
                final String modTranslatedName = split[1];

                if (StringUtils.isNullOrEmpty(modName) || StringUtils.isNullOrEmpty(modTranslatedName)) continue;

                final Mod mod = ModManager.Instance.getModFromName(modName);

                if (mod != null) {
                    mod.setTranslatedName(modTranslatedName);
                }
            }
        }
    }
}
