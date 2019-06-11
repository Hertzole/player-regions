package se.hertzole.playerregions.commands;

import org.bukkit.command.CommandSender;
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

@CommandInfo(name = "claim", pattern = "claim", permission = "playerregions.user.claim", usage = "/pr claim <name>", desc = "Claims a region after two points are set.")
public class ClaimCommand implements Command {


    @Override
    public boolean execute(HertzPlugin plugin, CommandSender sender, String... args) {
        PlayerRegions re = (PlayerRegions) plugin;
        Player player = (Player) sender;

        Setup setup = re.getSetupManager().getOrCreateSetup(player);
        setup.wantsToRemove = false;

        String displayName = String.join(" ", args);
        String regionId = displayName.replace(" ", "_").toLowerCase();

        if (re.getRegionManager().playerHasRegion(player, regionId)) {
            plugin.getGlobalMessenger().tell(sender, Msg.ALREADY_EXISTS.toString().replace("{region}", displayName));
            return true;
        }

        if (!setup.hasSetPos1) {
            plugin.getGlobalMessenger().tell(sender, Msg.MISSING_POS.toString().replace("{position}", "1"));
            return true;
        }

        if (!setup.hasSetPos2) {
            plugin.getGlobalMessenger().tell(sender, Msg.MISSING_POS.toString().replace("{position}", "2"));
            return true;
        }

        double costPerColumn = plugin.getConfig().getDouble("cost-per-column", 0);
        double totalCost = 0;

        double minX = setup.pos1.getX();
        double maxX = setup.pos2.getX();

        double minZ = setup.pos1.getZ();
        double maxZ = setup.pos2.getZ();

        if (minX > maxX) {
            double temp = minX;
            minX = maxX;
            maxX = temp;
        }

        if (minZ > maxZ) {
            double temp = minZ;
            minZ = maxZ;
            maxZ = temp;
        }

        for (int x = (int) minX; x <= maxX; x++) {
            for (int z = (int) minZ; z <= maxZ; z++) {
                totalCost += costPerColumn;
            }
        }

        double playerMoney = re.getEconomy().getBalance(player);
        setup.displayName = displayName;
        setup.id = regionId;
        setup.price = totalCost;
        setup.world = player.getWorld();

        if (totalCost <= playerMoney) {
            plugin.getGlobalMessenger().tell(sender, Msg.COST.toString().replace("{cost}", NumberUtil.toPrettyCurrency(totalCost)));
            setup.needsConfirm = true;
        } else {
            plugin.getGlobalMessenger().tell(sender, Msg.NOT_ENOUGH_MONEY.toString()
                    .replace("{cost}", NumberUtil.toPrettyCurrency(totalCost))
                    .replace("{money}", NumberUtil.toPrettyCurrency(playerMoney)));
            setup.needsConfirm = false;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, int argIndex) {
        return new ArrayList<>();
    }
}
