package se.hertzole.playerregions.commands;

import org.bukkit.command.CommandSender;
import se.hertzole.mchertzlib.HertzPlugin;
import se.hertzole.mchertzlib.commands.Command;
import se.hertzole.mchertzlib.commands.CommandInfo;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(name = "member", pattern = "member", usage = "/pr member", desc = "Add or remove members from your region",
        permission = "playerregions.user.member", console = false)
public class MemberCommand implements Command {

    @Override
    public boolean execute(HertzPlugin plugin, CommandSender sender, String... args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, int argIndex) {
        List<String> result = new ArrayList<>();

        if (argIndex == 0) {
            result.add("add");
            result.add("remove");
        }

        return result;
    }
}
