package se.hertzole.playerregions;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import se.hertzole.mchertzlib.utils.NumberUtil;
import se.hertzole.playerregions.data.Setup;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class RegionManager {

    private PlayerRegions plugin;

    private FileConfiguration regionsConfig = null;
    private File regionsConfigFile = null;

    private int minX;
    private int maxX;
    private int minZ;
    private int maxZ;
    private int maxClaims;

    private HashMap<Flag, Object> regionFlags;

    public RegionManager(PlayerRegions plugin) {
        this.plugin = plugin;
    }

    public void reloadConfig(FileConfiguration config) {
        reloadLimits(config);
        reloadFlags(config);
        reloadRegions();
    }

    private void reloadLimits(FileConfiguration config) {
        minX = config.getInt("min-size-x", 0);
        maxX = config.getInt("max-size-x", 0);
        minZ = config.getInt("min-size-z", 0);
        maxZ = config.getInt("max-size-z", 0);
        maxClaims = config.getInt("max-regions", 0);
    }

    private void reloadFlags(FileConfiguration config) {
        Map<String, Object> flagValues = config.getConfigurationSection("default-flags").getValues(true);

        regionFlags = new HashMap<>();

        int index = 0;

        for (String key : flagValues.keySet()) {
            String value = flagValues.values().toArray()[index].toString();
            try {
                Flag flag = Flags.fuzzyMatchFlag(WorldGuard.getInstance().getFlagRegistry(), key);
                Object state = getFlagType(value);
                regionFlags.put(flag, state);
            } catch (Exception e) {
                plugin.getGlobalMessenger().tellConsole("&c" + key + " flag does not exist.");
            }
            index++;
        }
    }

    private Object getFlagType(String value) {

        String trimmedValue = value.toLowerCase().trim();

        if (trimmedValue.equals("allow")) {
            return StateFlag.State.ALLOW;
        } else if (trimmedValue.equals("deny")) {
            return StateFlag.State.DENY;
        } else if (trimmedValue.equals("true")) {
            return true;
        } else if (trimmedValue.equals("false")) {
            return false;
        } else if (NumberUtil.canParseDouble(trimmedValue)) {
            return Double.parseDouble(trimmedValue);
        } else if (NumberUtil.canParseInteger(trimmedValue)) {
            return Integer.parseInt(trimmedValue);
        } else {
            return value;
        }
    }

    private void reloadRegions() {
        if (regionsConfigFile == null) {
            regionsConfigFile = new File(plugin.getDataFolder(), "regions.yml");
        }
        regionsConfig = YamlConfiguration.loadConfiguration(regionsConfigFile);
    }

    public FileConfiguration getRegionsConfig() {
        if (regionsConfig == null) {
            reloadRegions();
        }

        return regionsConfig;
    }

    public void saveRegionsConfig() {
        if (regionsConfig == null || regionsConfigFile == null) {
            return;
        }

        try {
            getRegionsConfig().save(regionsConfigFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save regions to " + regionsConfigFile, ex);
        }
    }

    public void addRegion(Setup setup, Player player) {
        BlockVector3 pos1 = BlockVector3.at(setup.pos1.getX(), 0, setup.pos1.getZ());
        BlockVector3 pos2 = BlockVector3.at(setup.pos2.getX(), 255, setup.pos2.getZ());

        // Create the region name.
        String regionName = player.getUniqueId() + "_" + setup.id.toLowerCase();

        // Create the region.
        ProtectedRegion region = new ProtectedCuboidRegion(regionName, pos1, pos2);

        // Add the player as the owner.
        DefaultDomain members = region.getMembers();
        members.addPlayer(player.getUniqueId());

        region.setOwners(members);

        for (Flag flag : regionFlags.keySet()) {
            if (flag != null) {
                Object value = regionFlags.get(flag);

                if (value instanceof String) {
                    value = ((String) value).replace("{player}", ChatColor.stripColor(player.getDisplayName()));
                }

                region.setFlag(flag, value);
            }
        }

        RegionContainer container = PlayerRegions.getWorldGuard().getPlatform().getRegionContainer();
        com.sk89q.worldguard.protection.managers.RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));
        if (regions != null)
            regions.addRegion(region);

        // Update the regions config file.
        FileConfiguration regionsConfig = getRegionsConfig();
        regionsConfig.set("players." + player.getUniqueId() + "." + setup.id + ".name", setup.displayName);
        regionsConfig.set("players." + player.getUniqueId() + "." + setup.id + ".id", regionName);
        regionsConfig.set("players." + player.getUniqueId() + "." + setup.id + ".world", setup.world.getName());

        // Save the regions.
        saveRegionsConfig();
        plugin.getSetupManager().removeSetup(player);

        plugin.getEconomy().withdrawPlayer(player, setup.price);
    }

    public void removeRegion(Setup setup, Player player) {
        if (playerHasRegion(player, setup.id)) {
            ConfigurationSection config = getRegionsConfig().getConfigurationSection("players." + player.getUniqueId() + "." + setup.id);
            RegionContainer container = PlayerRegions.getWorldGuard().getPlatform().getRegionContainer();
            com.sk89q.worldguard.protection.managers.RegionManager regions = container.get(BukkitAdapter.adapt(plugin.getServer().getWorld(config.getString("world", "world"))));
            if (regions != null) {
                ProtectedRegion region = regions.getRegion(config.getString("id"));
                if (region != null) {
                    regions.removeRegion(config.getString("id"), RemovalStrategy.UNSET_PARENT_IN_CHILDREN);
                }

                getRegionsConfig().set("players." + player.getUniqueId() + "." + setup.id, null);
                saveRegionsConfig();

                if (plugin.getConfig().getBoolean("get-refund")) {
                    plugin.getEconomy().depositPlayer(player, setup.price);
                }
            }
        }
    }

    public boolean overlapsUnownedRegion(Player player, double minX, double maxX, double minZ, double maxZ) {
        RegionContainer container = PlayerRegions.getWorldGuard().getPlatform().getRegionContainer();
        com.sk89q.worldguard.protection.managers.RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));

        if (regions == null)
            return false;

        BlockVector3 pos1 = BlockVector3.at(minX, 0, minZ);
        BlockVector3 pos2 = BlockVector3.at(maxX, 255, maxZ);

        ProtectedRegion region = new ProtectedCuboidRegion("temp", pos1, pos2);

        return regions.overlapsUnownedRegion(region, WorldGuardPlugin.inst().wrapPlayer(player));
    }

    public boolean playerHasRegion(Player player, String regionId) {
        List<String> regions = getPlayerRegionNames(player);
        if (regions == null || regions.size() == 0)
            return false;
        for (String i : regions) {
            if (i.trim().toLowerCase().equals(regionId.trim().toLowerCase()))
                return true;
        }

        return false;
    }

    public boolean playerHasRegionWithName(Player player, String regionName) {
        regionName = regionName.replace(" ", "_").toLowerCase();
        return playerHasRegion(player, regionName);
    }

    public List<String> getPlayerRegionNames(Player player) {
        List<String> result = new ArrayList<>();
        FileConfiguration regionsConfig = getRegionsConfig();
        if (regionsConfig == null)
            return result;
        if (!regionsConfig.contains("players." + player.getUniqueId()))
            return result;

        Set<String> regions = regionsConfig.getConfigurationSection("players." + player.getUniqueId()).getKeys(false);
        result.addAll(regions);
        Collections.sort(result);

        return result;
    }

    public List<String> getPlayerRegionPrettyNames(Player player) {
        List<String> result = new ArrayList<>();

        List<String> regionNames = getPlayerRegionNames(player);

        FileConfiguration regionsConfig = getRegionsConfig();

        for (int i = 0; i < regionNames.size(); i++) {
            result.add(regionsConfig.getString("players." + player.getUniqueId() + "." + regionNames.get(i) + ".name"));
        }

        return result;
    }

    public int getPlayerRegionCount(Player player) {
        int result = 0;

        FileConfiguration regionsConfig = getRegionsConfig();
        if (regionsConfig == null)
            return result;
        if (!regionsConfig.contains("players." + player.getUniqueId()))
            return result;

        result = regionsConfig.getConfigurationSection("players." + player.getUniqueId()).getKeys(false).size();

        return result;
    }

    public ProtectedRegion getRegion(Player player, String id) {
        ProtectedRegion result = null;

        com.sk89q.worldguard.protection.managers.RegionManager container = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
        if (container != null) {
            FileConfiguration config = getRegionsConfig();
            result = container.getRegion(config.getString("players." + player.getUniqueId() + "." + id + ".id"));
        }

        return result;
    }

    public ProtectedRegion getRegionWithName(Player player, String name) {
        name = name.replace(" ", "_").toLowerCase();
        return getRegion(player, name);
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public int getMaxClaims() {
        return maxClaims;
    }
}
