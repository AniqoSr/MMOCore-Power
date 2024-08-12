package org.justforfun;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlaceholderAPI extends PlaceholderExpansion {

    private final Main plugin;
    private final Config config;
    private final PlayerPowerData playerPowerData;

    public PlaceholderAPI(Main plugin, Config config, PlayerPowerData playerPowerData) {
        this.plugin = plugin;
        this.config = config;
        this.playerPowerData = playerPowerData;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "mmocore-power";
    }

    @Override
    public @NotNull String getAuthor() {
        return "JFF";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (player == null) {
            return "0";
        }

        UUID playerId = player.getUniqueId();

        if (identifier.equals("power_total")) {
            double totalPower = playerPowerData.getPowerTotal(playerId);
            return String.valueOf((int) totalPower);
        } else if (identifier.equals("attack_power")) {
            double attackPower = playerPowerData.getAttackPower(playerId);
            return String.valueOf((int) attackPower);
        } else if (identifier.equals("defense_power")) {
            double defensePower = playerPowerData.getDefensePower(playerId);
            return String.valueOf((int) defensePower);
        }

        return null;
    }
}