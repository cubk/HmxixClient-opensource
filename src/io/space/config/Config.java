package io.space.config;

import java.io.File;

public interface Config {
    String getName();

    String getPath();

    File getModFile();

    File getValueFile();

    File getDesignerFile();

    File getGlobalSettingFile();
}
