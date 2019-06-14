package se.hertzole.playerregions;

import com.sk89q.worldguard.WorldGuard;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import se.hertzole.mchertzlib.HertzPlugin;
import se.hertzole.mchertzlib.commands.BaseCommandHandler;
import se.hertzole.mchertzlib.messages.Messenger;
import se.hertzole.mchertzlib.utils.ConfigUtil;
import se.hertzole.playerregions.commands.CommandHandler;

import java.io.File;
import java.io.IOException;

public final class PlayerRegions extends HertzPlugin {

    private Economy economy;

    private SetupManager setupManager;
    private RegionManager regionManager;
    private PreviewManager previewManager;

    public static PlayerRegions instance;

    @Override
    protected void onEnabled() {
        instance = this;

        setupVault();
    }

    @Override
    protected void onDisabled() {
        previewManager = null;
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
        previewManager = new PreviewManager(this);
    }

    @Override
    protected void onReload() {
        reloadMessages();
    }

    private void reloadMessages() {
        File file = new File(getDataFolder(), "messages.yml");
        try {
            if (file.createNewFile()) {
                getLogger().info("messages.yml created");
                YamlConfiguration yaml = Msg.toYaml();
                yaml.save(file);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.load(file);
            ConfigUtil.addMissingRemoveObsolete(file, Msg.toYaml(), yaml);
            Msg.load(yaml);
        } catch (IOException e) {
            throw new RuntimeException("There was an error reading the messages-file.\n" + e.getMessage());
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException("\n\n>>>\n>>> There is an error in your messages-file! Handle it!\n>>> Here's what snakeyaml says:\n>>>\n\n" + e.getMessage());
        }
    }

    @Override
    protected void onReloadConfig(FileConfiguration config) {
        regionManager.reloadConfig(config);
    }

    @Override
    protected void reloadGlobalMessenger() {
        String prefix = config.getString("prefix", "&f[&aPlayer Regions&f]");

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

    public PreviewManager getPreviewManager() {
        return previewManager;
    }

    public Economy getEconomy() {
        return economy;
    }
}
