package io.space.config;

import io.space.Wrapper;

import java.io.File;

public final class CustomConfig implements Config {
    private final String configName;
    private final String path;
    private final File modFile;
    private final File valueFile;
    private final File designerFile;
    private final File globalSettingFile;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public CustomConfig(String configName) {
        this.configName = configName;

        final File pathFile = new File(Wrapper.Instance.getClientDirectory().getAbsolutePath(),"configs/split_" + configName + "/");

        pathFile.mkdirs();

        this.path = pathFile.getAbsolutePath();
        this.modFile = new File(pathFile,"mod.str");
        this.valueFile = new File(pathFile,"value.str");
        this.designerFile = new File(pathFile,"designer.str");
        this.globalSettingFile = new File(pathFile,"globalsetting.str");
    }

    @Override
    public String getName() {
        return configName;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public File getModFile() {
        return modFile;
    }

    @Override
    public File getValueFile() {
        return valueFile;
    }

    @Override
    public File getDesignerFile() {
        return designerFile;
    }

    @Override
    public File getGlobalSettingFile() {
        return globalSettingFile;
    }
}
