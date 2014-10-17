package me.hadrondev.commands;

import me.hadrondev.BungeeEssentials;
import me.hadrondev.Chat;
import me.hadrondev.permissions.Permission;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/**
 * Created by Connor Harries on 17/10/2014.
 */
public class Reply extends Command {
    public Reply(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if(sender instanceof ProxiedPlayer && strings != null) {
            if (strings.length > 0) {
                ProxiedPlayer player = (ProxiedPlayer)sender;
                if(Permission.has(sender, Permission.MESSAGE)) {
                    StringBuilder builder = new StringBuilder();
                    for (String s : strings) {
                        builder.append(s).append(" ");
                    }

                    UUID uuid = Chat.reply(player);

                    ProxiedPlayer recipient = BungeeEssentials.me.getProxy().getPlayer(uuid);

                    if (recipient != null) {
                        Chat.sendMessage(player, recipient.getName(), builder.toString());
                    } else {
                        sender.sendMessage(new ComponentBuilder("Sorry, that player is offline.")
                            .color(ChatColor.RED).create());
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
