package se.hertzole.playerregions.commands;

import se.hertzole.mchertzlib.HertzPlugin;
import se.hertzole.mchertzlib.commands.BaseCommandHandler;

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
        return "playerregions.admin.reload";
    }

    @Override
    protected String getHelpMessage() {
        return null;
    }

    @Override
    protected String getMultipleMatchesMessage() {
        return null;
    }

    @Override
    protected String getUnknownCommandMessage() {
        return null;
    }

    @Override
    protected String getNoPermissionMessage() {
        return null;
    }
}
