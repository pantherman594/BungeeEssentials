package me.hadrondev.commands;

import me.hadrondev.BungeeEssentials;
import me.hadrondev.permissions.Permission;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

@SuppressWarnings("deprecation")
public class Send extends Command {
    public Send(String name) {
        super(name);
    }

    @Override
    public void execute(final CommandSender sender, String[] strings) {
        if(Permission.has(sender, Permission.ADMIN_SEND)) {
            if(strings.length > 1) {
                final ProxiedPlayer player = BungeeEssentials.me.getProxy().getPlayer(strings[0]);
                if(player != null) {
                    sender.sendMessage(
                        new ComponentBuilder("Sending ").color(ChatColor.GREEN).append(strings[0])
                            .color(ChatColor.YELLOW).append(" to ").color(ChatColor.GREEN)
                            .append(strings[1]).create());
                    player.connect(BungeeEssentials.me.getProxy().getServerInfo(strings[1]),
                        new Callback<Boolean>() {
                            @Override public void done(Boolean success, Throwable throwable) {
                                if (success) {
                                    player.sendMessage(new ComponentBuilder("Whooooooooooosh!")
                                        .color(ChatColor.LIGHT_PURPLE).create());
                                } else {
                                    sender.sendMessage(ChatColor.RED + "Unable to send player to server.");
                                }
                            }
                        });
                } else {
                    sender.sendMessage(new ComponentBuilder("Sorry, that player is offline.").color(ChatColor.RED).create());
                }
            } else {
                sender.sendMessage(
                    new ComponentBuilder("Invalid arguments provided.").color(ChatColor.RED)
                        .create());
            }
        } else {
            sender.sendMessage(new ComponentBuilder("You do not have permission to do that.").color(ChatColor.RED).create());
        }
    }
}
