package org.justforfun;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.rockyhawk.commandpanels.api.CommandPanelsAPI;
import me.rockyhawk.commandpanels.api.Panel;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AnotherPlaceholder extends PlaceholderExpansion {

    private final CommandPanelsAPI api;
    private final Main plugin;

    public AnotherPlaceholder(Main plugin, CommandPanelsAPI api) {
        this.api = api;
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "panel";
    }

    @Override
    public @NotNull String getAuthor() {
        return "JFF"; // Ganti dengan nama Anda
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0"; // Ganti dengan versi plugin Anda
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true; // Return true if you want PlaceholderAPI to keep this expansion loaded
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        // Cek apakah pemain valid dan sedang online
        if (player == null || !player.isOnline()) {
            return null; // Mengembalikan null jika pemain tidak online atau tidak valid
        }

        // Cast pemain ke Player karena kita membutuhkan metode dari kelas Player
        Player onlinePlayer = (Player) player;

        // Placeholder untuk menampilkan nama GUI yang sedang dibuka
        if (params.equals("current")) {
            Panel panel = api.getOpenPanel(onlinePlayer, PanelPosition.Top);
            if (panel != null && api.isPanelOpen(onlinePlayer)) {
                return panel.getName(); // Mengembalikan nama panel
            } else {
                return "No GUI Opened"; // Tidak ada GUI yang terbuka
            }
        }

        return null; // Mengembalikan null jika placeholder tidak dikenali
    }
}