package org.justforfun;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class PlaceholderAPI extends PlaceholderExpansion {

    private final Main plugin;
    private final Config config;
    private final Data topPlayersData;

    public PlaceholderAPI(Main plugin, Config config, Data topPlayersData) {
        this.plugin = plugin;
        this.config = config;
        this.topPlayersData = topPlayersData;
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
        if (identifier.equals("power_total")) {
            double totalPower = calculatePlayerPower(player);
            return String.valueOf((int) totalPower);
        } else if (identifier.startsWith("power_top_")) {
            int rank;
            try {
                rank = Integer.parseInt(identifier.substring(10));
            } catch (NumberFormatException e) {
                return "Invalid rank";
            }

            if (rank < 1 || rank > 10) {
                return "Invalid rank";
            }

            List<Map.Entry<OfflinePlayer, Double>> topPlayers = getTopPlayersByPower();
            if (rank <= topPlayers.size()) {
                return topPlayers.get(rank - 1).getKey().getName() + " - " + topPlayers.get(rank - 1).getValue().intValue();
            } else {
                return "None - 0";
            }
        } else if (identifier.equals("attack_power")) {
            double attackPower = calculateAttackPower(player);
            return String.valueOf((int) attackPower);
        } else if (identifier.equals("defense_power")) {
            double defensePower = calculateDefensePower(player);
            return String.valueOf((int) defensePower);
        }

        return null;
    }

    private List<Map.Entry<OfflinePlayer, Double>> getTopPlayersByPower() {
        Map<OfflinePlayer, Double> playerPowerMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : topPlayersData.getTopPlayers().entrySet()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(entry.getKey()));
            playerPowerMap.put(player, entry.getValue());
        }

        return playerPowerMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toList());
    }

    private double calculatePlayerPower(OfflinePlayer player) {
        double totalPower = 0;

        totalPower += calculateAttackPower(player);
        totalPower += calculateDefensePower(player);

        topPlayersData.addPlayer(player, totalPower);

        return totalPower;
    }

    private double calculateAttackPower(OfflinePlayer player) {
        double attackPower = 0;

        // %mmocore_stat_skill_critical_strike_power% : 1.8 = totalSkillCriticalStrike
        double totalSkillCriticalStrike = parsePlaceholder(player, "mmocore_stat_skill_critical_strike_power") / 1.8;
        attackPower += totalSkillCriticalStrike;

        // %mmocore_stat_critical_strike_power% : 1.8 = totalCriticalStrike
        double totalCriticalStrike = parsePlaceholder(player, "mmocore_stat_critical_strike_power") / 1.8;
        attackPower += totalCriticalStrike;

        // Add other attack stats directly
        attackPower += parsePlaceholder(player, "mmocore_stat_magic_damage");
        attackPower += parsePlaceholder(player, "mmocore_stat_physical_damage");
        attackPower += parsePlaceholder(player, "mmocore_stat_projectile_damage");
        attackPower += parsePlaceholder(player, "mmocore_stat_weapon_damage");
        attackPower += parsePlaceholder(player, "mmocore_stat_skill_damage");
        attackPower += parsePlaceholder(player, "mmocore_stat_undead_damage");
        attackPower += parsePlaceholder(player, "mmocore_stat_pvp_damage");
        attackPower += parsePlaceholder(player, "mmocore_stat_pve_damage");
        attackPower += parsePlaceholder(player, "mmocore_stat_attack_damage");
        attackPower += parsePlaceholder(player, "mmocore_stat_critical_strike_chance");
        attackPower += parsePlaceholder(player, "mmocore_stat_skill_critical_strike_chance");
        attackPower += parsePlaceholder(player, "mmocore_stat_attack_speed");

        // Add element stats from config
        for (String element : config.getElements()) {
            attackPower += parsePlaceholder(player, "mmocore_stat_" + element + "_damage");
            attackPower += parsePlaceholder(player, "mmocore_stat_" + element + "_weakness");
        }

        return attackPower;
    }

    private double calculateDefensePower(OfflinePlayer player) {
        double defensePower = 0;

        // %mmocore_stat_max_health% : 2.5 = <%mmocore_stat_armor% is integer>percent% x 2 = totalHealth
        double maxHealth = parsePlaceholder(player, "mmocore_stat_max_health") / 2.5;
        double armor = parsePlaceholder(player, "mmocore_stat_armor");
        double armorPercent = (int) armor / 100.0; // Convert to percentage
        double totalHealth = maxHealth + (armorPercent * 2);
        defensePower += totalHealth;

        // Add other defense stats directly
        defensePower += parsePlaceholder(player, "mmocore_stat_block_power");
        defensePower += parsePlaceholder(player, "mmocore_stat_block_rating");
        defensePower += parsePlaceholder(player, "mmocore_stat_dodge_rating");
        defensePower += parsePlaceholder(player, "mmocore_stat_parry_rating");
        defensePower += parsePlaceholder(player, "mmocore_stat_fall_damage_reduction");
        defensePower += parsePlaceholder(player, "mmocore_stat_projectile_damage_reduction");
        defensePower += parsePlaceholder(player, "mmocore_stat_physical_damage_reduction");
        defensePower += parsePlaceholder(player, "mmocore_stat_fire_damage_reduction");
        defensePower += parsePlaceholder(player, "mmocore_stat_pvp_damage_reduction");
        defensePower += parsePlaceholder(player, "mmocore_stat_pve_damage_reduction");
        defensePower += parsePlaceholder(player, "mmocore_stat_damage_reduction");
        defensePower += parsePlaceholder(player, "mmocore_stat_magic_damage_reduction");
        defensePower += parsePlaceholder(player, "mmocore_stat_armor_toughness");
        defensePower += parsePlaceholder(player, "mmocore_stat_knockback_resistance");
        defensePower += parsePlaceholder(player, "mmocore_stat_defense");

        // Add element stats from config
        for (String element : config.getElements()) {
            defensePower += parsePlaceholder(player, "mmocore_stat_" + element + "_defense");
        }

        return defensePower;
    }

    private double parsePlaceholder(OfflinePlayer player, String placeholder) {
        String value = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, "%" + placeholder + "%");
        if (value == null || value.isEmpty() || value.equalsIgnoreCase("none")) {
            return 0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}