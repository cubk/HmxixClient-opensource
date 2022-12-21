package io.space.config;

import io.space.Wrapper;

import java.io.File;

public final class NormalConfig implements Config {
    private final String path = Wrapper.Instance.getClientDirectory().getAbsolutePath();
    private final File modFile = new File(Wrapper.Instance.getClientDirectory(),"mod.str");
    private final File valueFile = new File(Wrapper.Instance.getClientDirectory(),"value.str");
    private final File designerFile = new File(Wrapper.Instance.getClientDirectory(),"designer.str");
    private final File globalSettingFile = new File(Wrapper.Instance.getClientDirectory(),"globalsetting.str");

    @Override
    public String getName() {
        return "NormalConfig";
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
