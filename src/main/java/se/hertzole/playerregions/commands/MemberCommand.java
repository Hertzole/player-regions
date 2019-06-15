package se.hertzole.playerregions.commands;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.hertzole.mchertzlib.HertzPlugin;
import se.hertzole.mchertzlib.commands.Command;
import se.hertzole.mchertzlib.commands.CommandInfo;
import se.hertzole.playerregions.Msg;
import se.hertzole.playerregions.PlayerRegions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CommandInfo(name = "member", pattern = "member", usage = "/pr member <add:remove> <player> <region>", desc = "Add or remove members from your region",
        permission = "playerregions.user.member", console = false)
public class MemberCommand implements Command {

    @Override
    public boolean execute(HertzPlugin plugin, CommandSender sender, String... args) {

        Player player = (Player) sender;

        if (args == null || args.length == 0) {
            return false;
        }

        String actionArg = args[0].toLowerCase().trim();

        if (actionArg.isEmpty() || (!actionArg.equals("add") && !actionArg.equals("remove"))) {
            plugin.getGlobalMessenger().tell(sender, Msg.MEMBER_ADD_OR_REMOVE);
            return true;
        }

        if (args.length < 2 || args[1].isEmpty()) {
            plugin.getGlobalMessenger().tell(sender, Msg.MEMBER_SPECIFY_PLAYER);
            return true;
        }

        Player affectedPlayer = plugin.getServer().getPlayer(args[1]);
        UUID affectedPlayerId = null;
        boolean foundPlayer = affectedPlayer != null;
        String playerName = "";
        if (affectedPlayer != null) {
            affectedPlayerId = affectedPlayer.getUniqueId();
            playerName = ChatColor.stripColor(affectedPlayer.getDisplayName());
        }

        if (!foundPlayer) {
            OfflinePlayer[] offlinePlayers = plugin.getServer().getOfflinePlayers();
            for (OfflinePlayer offlinePlayer : offlinePlayers) {
                if (offlinePlayer.getName().toLowerCase().equals(args[1].toLowerCase().trim())) {
                    affectedPlayerId = offlinePlayer.getUniqueId();
                    playerName = ChatColor.stripColor(offlinePlayer.getName());
                    foundPlayer = true;
                    break;
                }
            }
        }

        if (!foundPlayer) {
            plugin.getGlobalMessenger().tell(sender, Msg.MEMBER_NO_PLAYER);
            return true;
        }

        if (affectedPlayerId.equals(player.getUniqueId())) {
            plugin.getGlobalMessenger().tell(sender, Msg.MEMBER_YOURSELF);
            return true;
        }

        String destination = "";
        boolean removePlayer = false;

        if (actionArg.equals("add")) {
            destination = "to";
        } else if (actionArg.equals("remove")) {
            destination = "from";
            removePlayer = true;
        }

        if (args.length < 3 || args[2].isEmpty()) {
            plugin.getGlobalMessenger().tell(sender, Msg.MEMBER_SPECIFY_REGION.toString().replace("{action}", actionArg).replace("{destination}", destination));
            return true;
        }

        if (!PlayerRegions.instance.getRegionManager().playerHasRegionWithName(player, args[2])) {
            plugin.getGlobalMessenger().tell(player, Msg.NO_REGION.toString().replace("{region}", args[2]));
            return true;
        }

        ProtectedRegion region = PlayerRegions.instance.getRegionManager().getRegionWithName(player, args[2]);
        DefaultDomain domain = region.getMembers();
        String msg;
        if (removePlayer) {
            domain.removePlayer(affectedPlayerId);
            msg = Msg.MEMBER_REMOVE.toString();
        } else {
            domain.addPlayer(affectedPlayerId);
            msg = Msg.MEMBER_ADD.toString();
        }

        region.setMembers(domain);
        plugin.getGlobalMessenger().tell(sender, msg.replace("{player}", playerName.replace("{region}", args[2])));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, int argIndex) {
        List<String> result = new ArrayList<>();

        if (argIndex == 0) {
            result.add("add");
            result.add("remove");
        } else if (argIndex == 1) {
            for (Player player : PlayerRegions.instance.getServer().getOnlinePlayers()) {
                result.add(ChatColor.stripColor(player.getDisplayName()));
            }
        } else if (argIndex == 2) {
            result.addAll(PlayerRegions.instance.getRegionManager().getPlayerRegionPrettyNames((Player) sender));
        }

        return result;
    }
}
