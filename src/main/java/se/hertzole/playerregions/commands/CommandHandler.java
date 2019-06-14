package se.hertzole.playerregions.commands;

import se.hertzole.mchertzlib.HertzPlugin;
import se.hertzole.mchertzlib.commands.BaseCommandHandler;
import se.hertzole.playerregions.Msg;

public class CommandHandler extends BaseCommandHandler {

    public CommandHandler(HertzPlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerCommands() {
        register(SetPosCommand.class);
        register(ClaimCommand.class);
        register(ConfirmCommand.class);
        register(DenyCommand.class);
        register(ListCommand.class);
        register(RemoveCommand.class);
    }

    @Override
    protected String getReloadPermission() {
        return "playerregions.admin.reloadConfig";
    }

    @Override
    protected String getHelpMessage() {
        return Msg.MISC_HELP.toString();
    }

    @Override
    protected String getMultipleMatchesMessage() {
        return Msg.MISC_MULTIPLE_MATCHES.toString();
    }

    @Override
    protected String getUnknownCommandMessage() {
        return Msg.MISC_NO_MATCHES.toString();
    }

    @Override
    protected String getNoPermissionMessage() {
        return Msg.MISC_NO_PERMISSION.toString();
    }

    @Override
    protected String getNoConsoleMessage() {
        return Msg.MISC_NOT_FROM_CONSOLE.toString();
    }
}
