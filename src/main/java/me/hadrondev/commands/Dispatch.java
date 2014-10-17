package me.hadrondev.commands;

import me.hadrondev.BungeeEssentials;
import me.hadrondev.permissions.Permission;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by Connor Harries on 17/10/2014.
 */
public class Dispatch extends Command {
    public Dispatch(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if(Permission.has(sender, Permission.ADMIN_DISPATCH)) {
            if(strings.length > 0) {

                StringBuilder builder = new StringBuilder();
                for(String s : strings) {
                    builder.append(s + " ");
                }

                for(ServerInfo server : BungeeEssentials.me.getProxy().getServers().values()) {
                    try {
                        server.sendData("gssentials.dispatch", builder.toString().getBytes("UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
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
