package se.hertzole.playerregions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import se.hertzole.playerregions.runnables.PreviewRunnable;

import java.util.List;

public class PreviewManager {

    private PlayerRegions plugin;

    private PreviewRunnable runnable;

    public PreviewManager(PlayerRegions plugin) {
        this.plugin = plugin;

        setupScheduler();
    }

    private void setupScheduler() {
        runnable = new PreviewRunnable();

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, runnable, 0, 20);
    }

    public void addPreview(Player player, List<Location> locations) {
        runnable.addPreview(player, locations);
    }

    public void removePreview(Player player) {
        runnable.removePreview(player);
    }
}
