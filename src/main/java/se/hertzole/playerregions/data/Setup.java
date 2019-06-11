package se.hertzole.playerregions.data;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Setup {

    public Location pos1;
    public Location pos2;

    public boolean hasSetPos1;
    public boolean hasSetPos2;

    public boolean needsConfirm;
    public String displayName;
    public String id;
    public double price;
    public World world;

    public boolean wantsToRemove;

    public Player player;

    public Setup(Player player) {
        this.player = player;
    }
}
