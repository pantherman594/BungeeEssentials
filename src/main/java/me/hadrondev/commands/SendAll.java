package me.hadrondev.commands;

import me.hadrondev.BungeeEssentials;
import me.hadrondev.permissions.Permission;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Connor Harries on 17/10/2014.
 */
public class SendAll extends Command {
    public SendAll(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if(Permission.has(sender, Permission.ADMIN_SENDALL)) {
            if(strings.length > 0) {
                ServerInfo info = BungeeEssentials.me.getProxy().getServerInfo(strings[1]);
                for(final ProxiedPlayer player : BungeeEssentials.me.getProxy().getPlayers()) {
                    player.connect(info, new Callback<Boolean>() {
                        @Override
                        public void done(Boolean success, Throwable throwable) {
                            if(success) {
                                player.sendMessage(new ComponentBuilder("Whooooooooooosh!").color(ChatColor.LIGHT_PURPLE).create());
                            }
                        }
                    });
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
