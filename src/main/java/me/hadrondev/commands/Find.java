package me.hadrondev.commands;

import me.hadrondev.BungeeEssentials;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Connor Harries on 17/10/2014.
 */
public class Find extends Command {
    public Find(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if(strings.length > 0) {
            ProxiedPlayer player = BungeeEssentials.me.getProxy().getPlayer(strings[0]);

            if(player != null) {
                ComponentBuilder builder = new ComponentBuilder(strings[0]);
                builder = builder.color(ChatColor.YELLOW);
                builder = builder.append(" is playing on ");
                builder = builder.color(ChatColor.GREEN);
                builder = builder.append(player.getServer().getInfo().getName());
                builder = builder.color(ChatColor.YELLOW);

                player.sendMessage(builder.create());
            } else {
                sender.sendMessage(new ComponentBuilder("Sorry, that player is offline.").color(ChatColor.RED).create());
            }
        }
    }
}
