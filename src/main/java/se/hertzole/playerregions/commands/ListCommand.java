package se.hertzole.playerregions.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import se.hertzole.mchertzlib.HertzPlugin;
import se.hertzole.mchertzlib.commands.Command;
import se.hertzole.mchertzlib.commands.CommandInfo;
import se.hertzole.playerregions.Msg;
import se.hertzole.playerregions.PlayerRegions;
import se.hertzole.playerregions.data.Setup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@CommandInfo(name = "list", pattern = "list", usage = "/pr list", desc = "Lists all your regions.",
        permission = "playerregions.user.list", console = false)
public class ListCommand implements Command {

    @Override
    public boolean execute(HertzPlugin plugin, CommandSender sender, String... args) {
        PlayerRegions pr = (PlayerRegions) plugin;
        Player player = (Player) sender;
        Setup setup = pr.getSetupManager().getSetup(player);

        FileConfiguration regionsConfig = pr.getRegionManager().getRegionsConfig();

        if (!regionsConfig.contains("players." + player.getUniqueId())) {
            plugin.getGlobalMessenger().tell(sender, Msg.NO_REGIONS);
        }

        ConfigurationSection playerRegions = regionsConfig.getConfigurationSection("players." + player.getUniqueId());
        if (playerRegions != null) {

            Set<String> regions = playerRegions.getKeys(false);
            if (regions.size() > 0) {
                StringBuilder sb = new StringBuilder("Your regions:");

                int index = 1;

                for (String i : regions) {
                    sb.append("\n&6[" + index + "]&r " + regionsConfig.getString("players." + player.getUniqueId() + "." + i + ".name"));
                    index++;
                }

                plugin.getGlobalMessenger().tell(sender, sb.toString());
            } else {
                plugin.getGlobalMessenger().tell(sender, Msg.NO_REGIONS);
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, int argIndex) {
        return new ArrayList<>();
    }
}
