package me.hadrondev.commands;

import me.hadrondev.BungeeEssentials;
import me.hadrondev.Chat;
import me.hadrondev.permissions.Permission;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Connor Harries on 17/10/2014.
 */
public class Message extends Command {
    public Message() {
        super("msg");
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if(sender instanceof ProxiedPlayer && strings != null) {
            if(strings.length > 1) {
                if(Permission.has(sender, Permission.MESSAGE)) {
                    ProxiedPlayer player = (ProxiedPlayer) sender;
                    ProxiedPlayer recipient = BungeeEssentials.me.getProxy().getPlayer(strings[0]);
                    if (recipient != null) {
                        StringBuilder builder = new StringBuilder();
                        for (String s : strings) {
                            if (!s.equals(strings[0])) {
                                builder.append(s).append(" ");
                            }
                        }
                        Chat.sendMessage(player, recipient.getName(), builder.toString());
                    } else {
                        sender.sendMessage(
                            new ComponentBuilder("Sorry, that player is offline.").color(
                                ChatColor.RED).create());
                    }
                }
            } else {
                sender.sendMessage(
                    new ComponentBuilder("Invalid arguments provided.").color(ChatColor.RED)
                        .create());
            }
        }
    }
}
