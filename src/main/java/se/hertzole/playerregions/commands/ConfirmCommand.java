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

@CommandInfo(name = "confirm", pattern = "confirm", usage = "/pr confirm", desc = "Confirms an action.",
        permission = "playerregions.user.confirm", console = false)
public class ConfirmCommand implements Command {

    @Override
    public boolean execute(HertzPlugin plugin, CommandSender sender, String... args) {
        PlayerRegions pr = (PlayerRegions) plugin;
        Player player = (Player) sender;
        Setup setup = pr.getSetupManager().getSetup(player);

        if (setup == null) {
            plugin.getGlobalMessenger().tell(sender, Msg.NO_SETUP);
            return true;
        }

        if (setup.wantsToRemove) {
            doRemove(setup, player, pr);
        } else {
            doClaim(setup, player, pr);
        }

        return true;
    }

    private void doRemove(Setup setup, Player player, PlayerRegions plugin) {
        plugin.getRegionManager().removeRegion(setup, player);
        plugin.getGlobalMessenger().tell(player, Msg.REMOVED.toString()
                .replace("{region}", setup.displayName)
                .replace("{refund}", NumberUtil.toPrettyCurrency(setup.price)));
    }

    private void doClaim(Setup setup, Player player, PlayerRegions plugin) {
        if (!setup.hasSetPos1 || !setup.hasSetPos2 || !setup.needsConfirm) {
            plugin.getGlobalMessenger().tell(player, Msg.INCOMPLETE_SETUP);
            return;
        }

        double playerMoney = plugin.getEconomy().getBalance(player);
        double totalCost = setup.price;

        if (totalCost > playerMoney) {
            plugin.getGlobalMessenger().tell(player, Msg.NOT_ENOUGH_MONEY.toString()
                    .replace("{cost}", NumberUtil.toPrettyCurrency(totalCost))
                    .replace("{money}", NumberUtil.toPrettyCurrency(playerMoney)));
            return;
        }

        plugin.getRegionManager().addRegion(setup, player);
        plugin.getGlobalMessenger().tell(player, Msg.CLAIMED.toString().replace("{region}", setup.displayName));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, int argIndex) {
        return new ArrayList<>();
    }
}
