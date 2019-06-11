package se.hertzole.playerregions.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import se.hertzole.mchertzlib.HertzPlugin;
import se.hertzole.mchertzlib.commands.Command;
import se.hertzole.mchertzlib.commands.CommandInfo;
import se.hertzole.mchertzlib.utils.NumberUtil;
import se.hertzole.playerregions.Msg;
import se.hertzole.playerregions.PlayerRegions;
import se.hertzole.playerregions.data.Setup;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(name = "remove", pattern = "remove", usage = "/pr remove <region>", desc = "Removes your region.", permission = "playerregions.user.remove")
public class RemoveCommand implements Command {

    @Override
    public boolean execute(HertzPlugin plugin, CommandSender sender, String... args) {
        PlayerRegions pr = (PlayerRegions) plugin;
        Player player = (Player) sender;

        FileConfiguration regionsConfig = pr.getRegionManager().getRegionsConfig();

        if (!regionsConfig.contains("players." + player.getUniqueId()) ||
                regionsConfig.getConfigurationSection("players." + player.getUniqueId()).getKeys(false).size() == 0) {
            plugin.getGlobalMessenger().tell(sender, Msg.NO_REGIONS);
            return true;
        }

        String displayName = String.join(" ", args);
        String id = String.join("_", args).toLowerCase();

        if (args.length == 0 || args[0] == null || args[0].isEmpty()) {
            plugin.getGlobalMessenger().tell(sender, Msg.NO_REMOVE_ARGUMENT);
            return true;
        }

        if (!pr.getRegionManager().playerHasRegion(player, id)) {
            plugin.getGlobalMessenger().tell(sender, Msg.NO_REGION.toString().replace("{region}", displayName));
            return true;
        }

        ConfigurationSection config = regionsConfig.getConfigurationSection("players." + player.getUniqueId() + "." + id);

        displayName = config.getString("name");

        double refundPrice = 0;

        if (plugin.getConfig().getBoolean("get-refund", true)) {
            RegionContainer container = PlayerRegions.getWorldGuard().getPlatform().getRegionContainer();
            RegionManager regions = container.get(BukkitAdapter.adapt(plugin.getServer().getWorld(config.getString("world", "world"))));
            if (regions != null) {
                ProtectedRegion region = regions.getRegion(config.getString("id"));
                if (region != null) {
                    BlockVector3 min = region.getMinimumPoint();
                    BlockVector3 max = region.getMaximumPoint();

                    for (int x = min.getX(); x <= max.getX(); x++) {
                        for (int z = min.getZ(); z <= max.getZ(); z++) {
                            refundPrice += pr.getConfig().getDouble("cost-per-column", 0);
                        }
                    }

                    refundPrice *= pr.getConfig().getDouble("refund-percent", 0.5);
                }
            }
        }

        Setup setup = pr.getSetupManager().getOrCreateSetup(player);
        setup.displayName = displayName;
        setup.id = id;
        setup.wantsToRemove = true;
        setup.price = refundPrice;

        plugin.getGlobalMessenger().tell(sender, Msg.REMOVE.toString().replace("{region}", displayName).replace("{refund}", NumberUtil.toPrettyCurrency(refundPrice)));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, int argIndex) {
        if (argIndex == 0) {
            List<String> result = PlayerRegions.instance.getRegionManager().getPlayerRegionNames((Player) sender);

            return result;
        } else {
            return new ArrayList<>();
        }
    }
}
