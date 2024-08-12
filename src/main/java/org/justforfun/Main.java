package org.justforfun;

import me.rockyhawk.commandpanels.api.CommandPanelsAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class Main extends JavaPlugin implements CommandExecutor, TabCompleter {

    private static final Logger LOGGER = Logger.getLogger("MMOCore Power");
    private Config config;
    private PlayerPowerData playerPowerData;
    private CommandPanelsAPI api;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = new Config(this);
        playerPowerData = new PlayerPowerData();
        new PlaceholderAPI(this, config, playerPowerData).register();
        new AnotherPlaceholder(this, api).register();
        getCommand("mmocore-power").setExecutor(this);
        getCommand("mmocore-power").setTabCompleter(this);

        // Schedule the task to run every 20 ticks (1 second)
        new PowerUpdateTask(this, playerPowerData, config).runTaskTimer(this, 0L, 20L);
        LOGGER.info("Plugin started successfully.");
    }

    @Override
    public void onDisable() {
        LOGGER.info("Plugin stopped successfully.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.isOp()) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }
            config.reloadConfig();
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
}