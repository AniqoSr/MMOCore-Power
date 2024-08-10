package org.justforfun;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin implements CommandExecutor, TabCompleter {

    private static final Logger LOGGER = Logger.getLogger("MMOCore Power");
    private Config config;
    private Data topPlayersData;
    private File dataFile;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = new Config(this);
        dataFile = new File(getDataFolder(), "data.json");
        createDataFileIfNotExists();
        loadTopPlayersData();
        new PlaceholderAPI(this, config, topPlayersData).register();
        getCommand("mmocore-power").setExecutor((sender, command, label, args) -> {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.isOp()) {
                    sender.sendMessage("You do not have permission to use this command.");
                    return true;
                }
                config.reloadConfig();
                LOGGER.info("Config reloaded successfully.");
                sender.sendMessage("Config reloaded successfully.");
                return true;
            }
            return false;
        });
        getCommand("mmocore-power").setTabCompleter(this);
        LOGGER.info("Plugin started successfully.");
    }

    @Override
    public void onDisable() {
        saveTopPlayersData();
        LOGGER.info("Plugin shut down successfully.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.isOp()) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }
            config.reloadConfig();
            LOGGER.info("Config reloaded successfully.");
            sender.sendMessage("Config reloaded successfully.");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("reload");
        }
        return completions;
    }

    private void createDataFileIfNotExists() {
        if (!dataFile.exists()) {
            getDataFolder().mkdirs();
            try {
                dataFile.createNewFile();
                topPlayersData = new Data();
                saveTopPlayersData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadTopPlayersData() {
        try (Reader reader = new FileReader(dataFile)) {
            Gson gson = new Gson();
            topPlayersData = gson.fromJson(reader, Data.class);
        } catch (IOException e) {
            e.printStackTrace();
            topPlayersData = new Data();
        }
    }

    private void saveTopPlayersData() {
        try (Writer writer = new FileWriter(dataFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(topPlayersData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}