package io.space.config;

import com.google.gson.*;
import com.google.gson.stream.MalformedJsonException;
import io.space.Wrapper;
import io.space.designer.Designer;
import io.space.designer.DesignerManager;
import io.space.global.GlobalSetting;
import io.space.mod.Mod;
import io.space.mod.ModManager;
import io.space.utils.RenderUtils;
import io.space.value.Value;
import io.space.value.ValueType;
import io.space.value.values.*;
import utils.hodgepodge.io.FileUtils;
import utils.hodgepodge.io.IOUtils;
import utils.hodgepodge.object.StringUtils;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ConfigManager {
    public static ConfigManager Instance;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final CopyOnWriteArrayList<CustomConfig> customConfigList = new CopyOnWriteArrayList<>();

    public ConfigManager() {
        final File file = new File(Wrapper.Instance.getClientDirectory(), "configs/");
        final File[] files = file.listFiles();

        if (files != null) {
            //noinspection ConstantConditions
            for (File listFile : file.listFiles()) {
                final String name = listFile.getName();

                if (listFile.isDirectory() && name.startsWith("split_")) {
                    final String configName = name.substring(6);
                    customConfigList.add(new CustomConfig(configName));
                }
            }
        }
    }

    public void saveConfig(Config config) throws IOException {
        final JsonArray modArray = new JsonArray();

        for (Mod mod : ModManager.Instance.getModMap().values()) {
            final JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("ModName",mod.getModName());
            jsonObject.addProperty("Enabled",mod.isEnable());
            jsonObject.addProperty("Key",mod.getKeyCode());

            final JsonArray jsonArray = new JsonArray();

            for (Value<?> value : mod.getValues()) {
                final JsonObject valueObject = new JsonObject();
                valueObject.addProperty("ValueType",value.getValueType().toString());
                valueObject.addProperty("ValueName",value.getValueName());

                switch (value.getValueType()) {
                    case BOOLEAN_VALUE:
                        valueObject.addProperty("Value",(Boolean) value.getValue());
                        break;
                    case NUMBER_VALUE:
                        valueObject.addProperty("Value",(Double) value.getValue());
                        break;
                    case MODE_VALUE:
                    case TEXT_VALUE:
                        valueObject.addProperty("Value",(String) value.getValue());
                        break;
                    case COLOR_VALUE:
                        valueObject.addProperty("Value",(Integer) value.getValue());
                        break;
                }

                jsonArray.add(valueObject);
            }

            jsonObject.add("Values",jsonArray);

            modArray.add(jsonObject);
        }

        writeStringToFile(gson.toJson(modArray),config.getModFile());

        final JsonArray designerArray = new JsonArray();

        for (Designer designer : DesignerManager.Instance.getDesigners()) {
            final JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("Type",designer.getType().toString());
            jsonObject.addProperty("X",designer.getX());
            jsonObject.addProperty("Y",designer.getY());

            designerArray.add(jsonObject);
        }

        writeStringToFile(gson.toJson(designerArray),config.getDesignerFile());

        final JsonArray globalArray = new JsonArray();

        for (BooleanValue value : GlobalSetting.Instance.getValues().values()) {
            final JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("Name",value.getValueName());
            jsonObject.addProperty("Enabled",value.getValue());

            globalArray.add(jsonObject);
        }

        writeStringToFile(gson.toJson(globalArray),config.getGlobalSettingFile());

        if (config instanceof CustomConfig && !customConfigList.contains(config)) {
            customConfigList.add((CustomConfig) config);
        }
    }

    private void writeStringToFile(String str,File file) throws IOException {
        try (final FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(str.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void removeCustomConfig(CustomConfig config) {
        final File file = new File(config.getPath());

        customConfigList.remove(config);
        FileUtils.deleteFile(file);
    }

    public void loadConfig(Config config) throws IOException {
        try {
            final JsonParser parser = new JsonParser();

            if (config.getModFile().exists()) {
                final JsonArray modArray = parser.parse(IOUtils.inputStreamToString(new FileInputStream(config.getModFile()), StandardCharsets.UTF_8)).getAsJsonArray();

                for (JsonElement jsonElement : modArray) {
                    final JsonObject jsonObject = jsonElement.getAsJsonObject();

                    final String modName = jsonObject.get("ModName").getAsString();

                    final Mod mod = ModManager.Instance.getModFromName(modName);

                    if (mod != null) {
                        mod.setEnable(jsonObject.get("Enabled").getAsBoolean());
                        mod.setKeyCode(jsonObject.get("Key").getAsInt());

                        final JsonArray values = jsonObject.get("Values").getAsJsonArray();

                        if (values.size() != 0) {
                            for (JsonElement valueElement : values) {
                                final JsonObject valueObject = valueElement.getAsJsonObject();

                                for (Value<?> value : mod.getValues()) {
                                    final ValueType valueType = ValueType.get(valueObject.get("ValueType").getAsString());
                                    final JsonElement currentValue = valueObject.get("Value");

                                    if (value.getValueName().equals(valueObject.get("ValueName").getAsString())) {
                                        switch (valueType) {
                                            case BOOLEAN_VALUE:
                                                ((BooleanValue) value).setValueDirect(currentValue.getAsBoolean());
                                                break;
                                            case NUMBER_VALUE:
                                                ((NumberValue) value).setValueDirect(currentValue.getAsDouble());
                                                break;
                                            case MODE_VALUE:
                                                ((ModeValue) value).setValueDirect(currentValue.getAsString());
                                                break;
                                            case TEXT_VALUE:
                                                ((TextValue) value).setValueDirect(currentValue.getAsString());
                                                ((TextValue) value).getGuiTextField().setText(currentValue.getAsString());
                                                break;
                                            case COLOR_VALUE:
                                                final ColorValue colorValue = (ColorValue) value;
                                                final int rgb = currentValue.getAsInt();
                                                final int[] ints = RenderUtils.splitRGB(rgb);
                                                final float[] hsb = Color.RGBtoHSB(ints[0],ints[1],ints[2], null);

                                                colorValue.setHue(hsb[0]);
                                                colorValue.setSaturation(hsb[1]);
                                                colorValue.setBrightness(hsb[2]);

                                                colorValue.setValueDirect(rgb);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (config.getDesignerFile().exists()) {
                for (JsonElement element : parser.parse(IOUtils.inputStreamToString(new FileInputStream(config.getDesignerFile()), StandardCharsets.UTF_8)).getAsJsonArray()) {
                    final JsonObject jsonObject = element.getAsJsonObject();
                    final Designer.Type type = Designer.Type.from(jsonObject.get("Type").getAsString());

                    for (Designer designer : DesignerManager.Instance.getDesigners()) {
                        if (designer.getType() == type) {
                            designer.setX((float) jsonObject.get("X").getAsDouble());
                            designer.setY((float) jsonObject.get("Y").getAsDouble());
                            break;
                        }
                    }
                }
            }

            if (config.getGlobalSettingFile().exists()) {
                for (JsonElement element : parser.parse(IOUtils.inputStreamToString(new FileInputStream(config.getGlobalSettingFile()), StandardCharsets.UTF_8)).getAsJsonArray()) {
                    final JsonObject jsonObject = element.getAsJsonObject();

                    final BooleanValue booleanValue = GlobalSetting.Instance.getValues().get(jsonObject.get("Name").getAsString());

                    if (booleanValue != null) {
                        booleanValue.setValueDirect(jsonObject.get("Enabled").getAsBoolean());
                    }
                }
            }
        } catch (MalformedJsonException | JsonSyntaxException e) {
            Wrapper.Instance.getLogger().info("Found old config: " + config.getName() + " try load");

            loadConfigOld(config);
        }
    }

    public void loadConfigOld(Config config) throws IOException {
        if (config.getModFile().exists()) {
            for (String str : IOUtils.inputStreamToStringLines(new FileInputStream(config.getModFile()), StandardCharsets.UTF_8)) {
                final String[] split = str.split(":");
                final String modName = split[0];
                final boolean modEnable = Boolean.parseBoolean(split[1]);
                final int modKey = Integer.parseInt(split[2]);

                final Mod mod = ModManager.Instance.getModFromName(modName);

                if (mod != null) {
                    mod.setEnable(modEnable);
                    mod.setKeyCode(modKey);
                }
            }
        }

        if (config.getValueFile().exists()) {
            for (String string : IOUtils.inputStreamToStringLines(new FileInputStream(config.getValueFile()), StandardCharsets.UTF_8)) {
                if (!StringUtils.isNullOrEmpty(string)) {
                    final String[] split = string.split(":");
                    final Mod mod = ModManager.Instance.getModFromName(split[0]);

                    if (mod != null) {
                        final String type_s = split[1];
                        final String valueName = split[2];

                        String value;

                        try {
                            value = split[3];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            value = "";
                        }

                        for (Value<?> valueObj : mod.getValues()) {
                            ValueType valueType = ValueType.get(type_s);

                            if (valueType != ValueType.NULL && valueObj.getValueName().equals(valueName)) {
                                switch (valueType) {
                                    case BOOLEAN_VALUE:
                                        ((BooleanValue) valueObj).setValueDirect(Boolean.parseBoolean(value));
                                        break;
                                    case NUMBER_VALUE:
                                        ((NumberValue) valueObj).setValueDirect(Double.parseDouble(value));
                                        break;
                                    case MODE_VALUE:
                                        ((ModeValue) valueObj).setValueDirect(value);
                                        break;
                                    case TEXT_VALUE:
                                        ((TextValue) valueObj).setValueDirect(value);
                                        ((TextValue) valueObj).getGuiTextField().setText(value);
                                        break;
                                    case COLOR_VALUE:
                                        final ColorValue colorValue = (ColorValue) valueObj;
                                        final float hue = Float.parseFloat(split[4]);
                                        final float saturation = Float.parseFloat(split[5]);
                                        final float brightness = Float.parseFloat(split[6]);

                                        colorValue.setValueDirect(Integer.parseInt(value));
                                        colorValue.setHue(hue);
                                        colorValue.setSaturation(saturation);
                                        colorValue.setBrightness(brightness);
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (config.getDesignerFile().exists()) {
            for (String string : IOUtils.inputStreamToStringLines(new FileInputStream(config.getDesignerFile()), StandardCharsets.UTF_8)) {
                if (!StringUtils.isNullOrEmpty(string)) {
                    final String[] split = string.split(":");
                    final Designer.Type type = Designer.Type.from(split[0]);
                    final double x = Double.parseDouble(split[1]);
                    final double y = Double.parseDouble(split[2]);

                    for (Designer designer : DesignerManager.Instance.getDesigners()) {
                        if (designer.getType() == type) {
                            designer.setX((float) x);
                            designer.setY((float) y);
                            break;
                        }
                    }
                }
            }
        }

        if (config.getGlobalSettingFile().exists()) {
            for (String string : IOUtils.inputStreamToStringLines(new FileInputStream(config.getGlobalSettingFile()), StandardCharsets.UTF_8)) {
                if (!StringUtils.isNullOrEmpty(string)) {
                    final String[] split = string.split(":");
                    final String name = split[0];
                    final boolean enable = Boolean.parseBoolean(split[1]);

                    final BooleanValue booleanValue = GlobalSetting.Instance.getValues().get(name);

                    if (booleanValue != null) {
                        booleanValue.setValueDirect(enable);
                    }
                }
            }
        }
    }

    public boolean containsCustomConfig(String configName) {
        return customConfigList.stream().anyMatch(config -> config.getName().equals(configName));
    }

    public CopyOnWriteArrayList<CustomConfig> getCustomConfigList() {
        return customConfigList;
    }
}
