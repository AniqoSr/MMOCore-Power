package org.justforfun;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Config {

    private final Main plugin;
    private FileConfiguration config;
    private File configFile;

    public Config(Main plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        saveDefaultConfig();
        reloadConfig();
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public void saveConfig() {
        if (config == null || configFile == null) {
            return;
        }
        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config to " + configFile);
        }
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
    }

    public List<String> getElements() {
        return getConfig().getStringList("element");
    }
}