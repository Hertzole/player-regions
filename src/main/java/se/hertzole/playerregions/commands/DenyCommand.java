package se.hertzole.playerregions.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.hertzole.mchertzlib.HertzPlugin;
import se.hertzole.mchertzlib.commands.Command;
import se.hertzole.mchertzlib.commands.CommandInfo;
import se.hertzole.playerregions.Msg;
import se.hertzole.playerregions.PlayerRegions;
import se.hertzole.playerregions.data.Setup;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(name = "deny", pattern = "deny", usage = "/pr deny", desc = "Denies an action.", permission = "playerregions.user.deny")
public class DenyCommand implements Command {

    @Override
    public boolean execute(HertzPlugin plugin, CommandSender sender, String... args) {
        PlayerRegions pr = (PlayerRegions) plugin;
        Player player = (Player) sender;
        Setup setup = pr.getSetupManager().getSetup(player);

        if (setup == null) {
            plugin.getGlobalMessenger().tell(sender, Msg.NO_SETUP_DENY);
            return true;
        }

        if (!setup.hasSetPos1 || !setup.hasSetPos2 || !setup.needsConfirm) {
            plugin.getGlobalMessenger().tell(sender, Msg.INCOMPLETE_SETUP);
            return true;
        }

        pr.getSetupManager().removeSetup(player);

        plugin.getGlobalMessenger().tell(sender, Msg.DENIED);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, int argIndex) {
        return new ArrayList<>();
    }
}
