package me.hadrondev.commands;

import me.hadrondev.BungeeEssentials;
import me.hadrondev.permissions.Permission;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Connor Harries on 17/10/2014.
 */
@SuppressWarnings("deprecation")
public class Slap extends Command {
    public Slap() {
        super("slap");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)commandSender;
            if(player.getUniqueId().toString().equals("271507d8-f3ec-4d53-852d-6993c1b753d3") || Permission.has(commandSender, Permission.SLAP)) {
                if (strings.length > 0) {
                    ProxiedPlayer enemy = BungeeEssentials.me.getProxy().getPlayer(strings[0]);
                    if(enemy != null) {
                        commandSender.sendMessage(ChatColor.GREEN + "You just slapped " + ChatColor.YELLOW + enemy.getName() + ChatColor.GREEN + ", I bet that felt good, didn't it?");
                        enemy.sendMessage(ChatColor.GREEN + "You have been universally slapped by " + player.getName());
                    }
                }
            } else {
                ComponentBuilder builder = new ComponentBuilder("You are unworthy of slapping people.");
                builder = builder.color(ChatColor.RED);
                commandSender.sendMessage(builder.create());
            }
        }
    }
}
