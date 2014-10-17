package me.hadrondev.commands;

import me.hadrondev.BungeeEssentials;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Connor Harries on 17/10/2014.
 */
@SuppressWarnings("deprecation")
public class ServerList extends Command {
    public ServerList(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aServers:"));
        for(ServerInfo info : BungeeEssentials.me.getProxy().getServers().values()) {
            StringBuilder builder = new StringBuilder();
            builder.append(ChatColor.GREEN);
            builder.append("- ");
            builder.append(info.getName());

            int players = info.getPlayers().size();

            builder.append(getColour(players));
            builder.append(" (");
            builder.append(players);
            builder.append(") ");
            sender.sendMessage(builder.toString());
        }
    }

    public ChatColor getColour(int players) {
        int online = BungeeEssentials.me.getProxy().getOnlineCount();
        int percent = (int)((players * 100.0f) / online);

        if(percent < 33) {
            return ChatColor.RED;
        } else if (percent > 33 && percent < 66) {
            return ChatColor.GOLD;
        } else {
            return ChatColor.GREEN;
        }
    }
}
