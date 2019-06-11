package se.hertzole.playerregions;

import org.bukkit.entity.Player;
import se.hertzole.playerregions.data.Setup;

import java.util.HashMap;

public class SetupManager {

    private PlayerRegions plugin;

    private HashMap<Player, Setup> setupMap;


    public SetupManager(PlayerRegions plugin) {
        this.plugin = plugin;

        setupMap = new HashMap<>();
    }

    public Setup getOrCreateSetup(Player player) {
        if (setupMap.containsKey(player)) {
            return setupMap.get(player);
        } else {
            Setup setup = new Setup(player);
            setupMap.put(player, setup);
            return setup;
        }
    }

    public Setup getSetup(Player player) {
        return setupMap.getOrDefault(player, null);
    }

    public void removeSetup(Player player) {
        setupMap.remove(player);
    }
}
