package se.hertzole.playerregions;

import com.sk89q.worldguard.WorldGuard;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import se.hertzole.mchertzlib.HertzPlugin;
import se.hertzole.mchertzlib.commands.BaseCommandHandler;
import se.hertzole.mchertzlib.messages.Messenger;
import se.hertzole.playerregions.commands.CommandHandler;

public final class PlayerRegions extends HertzPlugin {

    private Economy economy;

    private SetupManager setupManager;
    private RegionManager regionManager;

    public static PlayerRegions instance;

    @Override
    protected void onEnabled() {
        instance = this;

        setupVault();
    }

    @Override
    protected BaseCommandHandler getCommandHandler() {
        return new CommandHandler(this);
    }

    @Override
    protected String getCommandPrefix() {
        return "pr";
    }

    @Override
    protected void onSetup() {
        setupManager = new SetupManager(this);
        regionManager = new RegionManager(this);

    }

    @Override
    protected void onReload() {
        regionManager.reloadRegions();
    }

    @Override
    protected void onReloadConfig(FileConfiguration config) {

    }

    @Override
    protected void reloadGlobalMessenger() {
        String prefix = config.getString("prefix", "&f[&9Custom Enchantments&f]");

        messenger = new Messenger(this, prefix);
    }

    private void setupVault() {
        Plugin vaultPlugin = this.getServer().getPluginManager().getPlugin("Vault");
        if (vaultPlugin == null) {
            messenger.tellConsole("&cVault was not found. PlayerRegions will not work properly.");
            return;
        }

        ServicesManager manager = this.getServer().getServicesManager();
        RegisteredServiceProvider<Economy> e = manager.getRegistration(Economy.class);

        if (e != null) {
            economy = e.getProvider();
            messenger.tellConsole("Vault registered!");
        } else {
            messenger.tellConsole("&cVault was found, but no economy plugin. PlayerRegions will not work properly.");
        }
    }

    public static WorldGuard getWorldGuard() {
        return WorldGuard.getInstance();
    }

    public SetupManager getSetupManager() {
        return setupManager;
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }

    public Economy getEconomy() {
        return economy;
    }
}
