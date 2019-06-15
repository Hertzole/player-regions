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

@CommandInfo(name = "pos", permission = "playerregions.user.pos", pattern = "pos", usage = "/pr pos <1:2>",
        desc = "Sets the two location to claim.", console = false)
public class SetPosCommand implements Command {

    @Override
    public boolean execute(HertzPlugin plugin, CommandSender sender, String... args) {
        PlayerRegions reg = (PlayerRegions) plugin;
        Player player = (Player) sender;

        if (args == null || args.length == 0 || args[0].isEmpty()) {
            plugin.getGlobalMessenger().tell(sender, Msg.INVALID_POS);
            return true;
        }

        String posArg = args[0];

        if (posArg.equals("1") || posArg.equals("2")) {
            boolean pos1 = posArg.equals("1");

            Setup setup = reg.getSetupManager().getOrCreateSetup(player);

            if (pos1) {
                setup.pos1 = player.getLocation();
                setup.hasSetPos1 = true;
            } else {
                setup.pos2 = player.getLocation();
                setup.hasSetPos2 = true;
            }

            plugin.getGlobalMessenger().tell(sender, Msg.SET_POS.toString().replace("{position}", posArg));

            return true;
        } else {
            plugin.getGlobalMessenger().tell(sender, Msg.INVALID_POS);
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, int argIndex) {
        List<String> suggestions = new ArrayList<>();

        if (argIndex == 0) {
            suggestions.add("1");
            suggestions.add("2");
        }

        return suggestions;
    }
}
