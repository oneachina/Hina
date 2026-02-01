/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.management.config;

import com.eatgrapes.hina.HinaClient;
import com.eatgrapes.hina.module.Module;
import com.eatgrapes.hina.setting.*;
import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;

public class ConfigManager {
    private final File configDir;
    private final File configFile;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ConfigManager() {
        Path gameDir = FabricLoader.getInstance().getGameDir();
        File hinaFolder = new File(gameDir.toFile(), "Hina");
        this.configDir = new File(hinaFolder, "config");
        this.configFile = new File(configDir, "config.json");
        if (!configDir.exists()) configDir.mkdirs();
    }

    public void save() {
        JsonObject json = new JsonObject();
        JsonArray modulesArray = new JsonArray();
        for (Module module : HinaClient.INSTANCE.moduleManager.getModules()) {
            JsonObject moduleJson = new JsonObject();
            moduleJson.addProperty("name", module.getName());
            moduleJson.addProperty("enabled", module.isEnabled());
            JsonObject settingsJson = getJsonObject(module);
            moduleJson.add("settings", settingsJson);
            modulesArray.add(moduleJson);
        }
        json.add("modules", modulesArray);
        try (Writer writer = new FileWriter(configFile)) {
            gson.toJson(json, writer);
        } catch (IOException ignored) {
        }
    }

    private static @NotNull JsonObject getJsonObject(Module module) {
        JsonObject settingsJson = new JsonObject();
        for (Setting<?> setting : module.getSettings()) {
            if (setting instanceof BooleanSetting) settingsJson.addProperty(setting.getName(), ((BooleanSetting) setting).getValue());
            else if (setting instanceof NumberSetting) settingsJson.addProperty(setting.getName(), ((NumberSetting) setting).getValue());
            else if (setting instanceof ModeSetting) settingsJson.addProperty(setting.getName(), ((ModeSetting) setting).getValue());
            else if (setting instanceof ColorSetting) settingsJson.addProperty(setting.getName(), ((ColorSetting) setting).getValue());
        }
        return settingsJson;
    }

    public void load() {
        if (!configFile.exists()) return;
        try (Reader reader = new FileReader(configFile)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            if (json.has("modules")) {
                JsonArray modulesArray = json.getAsJsonArray("modules");
                for (JsonElement element : modulesArray) {
                    JsonObject moduleJson = element.getAsJsonObject();
                    String name = moduleJson.get("name").getAsString();
                    Module module = HinaClient.INSTANCE.moduleManager.getModuleByName(name);
                    if (module != null) {
                        if (moduleJson.has("enabled")) module.setEnabled(moduleJson.get("enabled").getAsBoolean());
                        if (moduleJson.has("settings")) {
                            JsonObject settingsJson = moduleJson.getAsJsonObject("settings");
                            for (Setting<?> setting : module.getSettings()) {
                                if (settingsJson.has(setting.getName())) {
                                    JsonElement val = settingsJson.get(setting.getName());
                                    if (setting instanceof BooleanSetting) ((BooleanSetting) setting).setValue(val.getAsBoolean());
                                    else if (setting instanceof NumberSetting) ((NumberSetting) setting).setValue(val.getAsDouble());
                                    else if (setting instanceof ModeSetting) ((ModeSetting) setting).setValue(val.getAsString());
                                    else if (setting instanceof ColorSetting) ((ColorSetting) setting).setValue(val.getAsInt());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}