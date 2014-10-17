package me.hadrondev.commands;

import me.hadrondev.BungeeEssentials;
import me.hadrondev.permissions.Permission;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Connor Harries on 17/10/2014.
 */
@SuppressWarnings("deprecation")
public class Alert extends Command {
    public Alert(String name) {
        super(name);
    }

    @Override public void execute(CommandSender sender, String[] strings) {
        if(Permission.has(sender, Permission.ADMIN_ALERT)) {
            if(strings.length > 0) {

                StringBuilder builder = new StringBuilder();
                for(String s : strings) {
                    builder.append(s).append(" ");
                }

                String msg = ChatColor.translateAlternateColorCodes('&', "&8[&a&l+&8] &a" + builder.toString());

                BungeeEssentials.me.getProxy().broadcast(msg);
            } else {
                sender.sendMessage(
                    new ComponentBuilder("Invalid arguments provided.").color(ChatColor.RED)
                        .create());
            }
        } else {
            sender.sendMessage(new ComponentBuilder("You do not have permission to do that.").color(
                ChatColor.RED).create());
        }
    }
}
