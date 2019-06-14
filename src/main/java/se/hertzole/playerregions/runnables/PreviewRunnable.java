package se.hertzole.playerregions.runnables;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class PreviewRunnable implements Runnable {

    private HashMap<Player, List<Location>> previews;

    public PreviewRunnable() {
        previews = new HashMap<>();
    }

    @Override
    public void run() {
        for (Player player : previews.keySet()) {
            List<Location> locations = previews.get(player);
            for (Location location : locations) {
                player.spawnParticle(Particle.VILLAGER_HAPPY, location, 2);
            }
        }
    }

    public void addPreview(Player player, List<Location> locations) {
        if (previews.containsKey(player)) {
            previews.replace(player, locations);
        } else {
            previews.put(player, locations);
        }
    }

    public void removePreview(Player player) {
        previews.remove(player);
    }
}
