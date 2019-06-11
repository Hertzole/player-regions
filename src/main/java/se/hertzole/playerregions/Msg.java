package se.hertzole.playerregions;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import se.hertzole.mchertzlib.messages.Message;

public enum Msg implements Message {

    INVALID_POS("&cYou need to specify what position, either 1 or 2."),
    MISSING_POS("&cYou haven't set position {position}!"),
    ALREADY_EXISTS("&cYou already have a region named '{region}'!"),
    CLAIMED("&aYou've claimed a new region called '{region}'!"),
    DENIED("Cancelling the region claiming."),
    COST("Claiming this region will cost you ${cost}. \nWrite &a/pr confirm&r to claim or &c/pr deny&r to cancel."),
    NOT_ENOUGH_MONEY("&cYou don't have enough money to claim this area. It costs ${cost} but you only have ${money}."),
    NO_SETUP("&cYou have nothing to confirm."),
    NO_SETUP_DENY("&cYou're not even claiming a region!"),
    NO_REMOVE_ARGUMENT("&cYou need to specify a region to remove."),
    INCOMPLETE_SETUP("&cYou haven't completed the region setup!"),
    NO_REGIONS("&cYou don't have any claimed regions."),
    NO_REGION("&cYou don't have a claimed region called '{region}'."),
    REMOVE("You're about to remove '{region}'. You will be refunded &a${refund}&r. Write &a/pr confirm&r to proceed or &c/pr deny&r to cancel."),
    REMOVED("Successfully removed '{region}'! You were refunded &a${refund}&r."),
    SET_POS("&aSet position {position}.");

    private String value;

    Msg(String value) {
        set(value);
    }

    void set(String value) {
        this.value = value;
    }

    public String toString() {
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    public String format(String s) {
        return (s == null) ? "" : toString().replaceAll("%", s);
    }

    static void load(ConfigurationSection config) {
        for (Msg msg : values()) {
            String key = msg.name().toLowerCase().replace("_", "-");
            msg.set(config.getString(key, ""));
        }
    }

    static YamlConfiguration toYaml() {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Msg msg : values()) {
            String key = msg.name().replace("_", "-").toLowerCase();
            yaml.set(key, msg.value);
        }

        return yaml;
    }
}
