package se.hertzole.playerregions;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import se.hertzole.mchertzlib.messages.Message;

public enum Msg implements Message {

    INVALID_POS("&cYou need to specify what position, either 1 or 2."),
    MISSING_POS("&cYou haven't set position {position}!"),
    SET_POS("&aSet position {position}."),

    TOO_MANY_CLAIMS("&cYou can't claim any more regions! You can only have {max} regions!"),
    NO_CLAIM_NAME("&cYou need to provide a name for your region!"),
    INCOMPLETE_SETUP("&cYou haven't completed the region setup!"),
    ALREADY_EXISTS("&cYou already have a region named '{region}'!"),
    TOO_SMALL("&cThe region you're trying to claim is too small!"),
    TOO_BIG("&cThe region you're trying to claim is too big!"),
    OVERLAPS("&cYour region overlaps with someone else's region!"),
    NOT_ENOUGH_MONEY("&cYou don't have enough money to claim this area. It costs ${cost} but you only have ${money}."),
    COST("Claiming this region will cost you ${cost}. \nWrite &a/pr confirm&r to claim or &c/pr deny&r to cancel."),

    CLAIMED("&aYou've claimed a new region called '{region}'!"),
    DENY_CLAIM("Cancelling the region claiming."),

    NO_SETUP("&cYou have nothing to confirm."),
    NO_SETUP_DENY("&cYou're not even claiming a region!"),

    NO_REMOVE_ARGUMENT("&cYou need to specify a region to remove."),
    REMOVE("You're about to remove '{region}'. You will be refunded &a${refund}&r. Write &a/pr confirm&r to proceed or &c/pr deny&r to cancel."),
    REMOVED("Successfully removed '{region}'! You were refunded &a${refund}&r."),

    MEMBER_ADD_OR_REMOVE("&cYou need to specify if you want to 'add' or 'remove' a member."),
    MEMBER_SPECIFY_PLAYER("&cYou need to specify a player to add."),
    MEMBER_NO_PLAYER("&cThere's no player with that name on the server."),
    MEMBER_YOURSELF("&cYou can't add or remove yourself from your own region."),
    MEMBER_SPECIFY_REGION("&cYou need to specify what region you want to {action} this player {destination}."),
    MEMBER_ADD("&aAdded {player} as a member to {region}!"),
    MEMBER_REMOVE("&aRemoved {player} as a member from {region}!"),

    NO_REGIONS("&cYou don't have any claimed regions."),
    NO_REGION("&cYou don't have a claimed region called '{region}'."),

    MISC_NO_PERMISSION("&cYou don't have permission for this command."),
    MISC_MULTIPLE_MATCHES("Did you mean any of these commands?"),
    MISC_NO_MATCHES("Command not found. Type &a/pr help&r."),
    MISC_NOT_FROM_CONSOLE("&cYou can't use this command from the console."),
    MISC_HELP("For a list of commands, type &a/pr help/r.");

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

    public static void load(ConfigurationSection config) {
        for (Msg msg : values()) {
            String key = msg.name().toLowerCase().replace("_", "-");
            msg.set(config.getString(key, ""));
        }
    }

    public static YamlConfiguration toYaml() {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Msg msg : values()) {
            String key = msg.name().replace("_", "-").toLowerCase();
            yaml.set(key, msg.value);
        }

        return yaml;
    }
}
