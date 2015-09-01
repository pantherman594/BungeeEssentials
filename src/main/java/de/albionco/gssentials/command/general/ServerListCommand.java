package de.albionco.gssentials.command.general;

import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Messenger;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

@SuppressWarnings("deprecation")
public class ServerListCommand extends Command {

    public ServerListCommand() {
        super(BungeeEssentials.ServerList_MAIN, Permissions.General.LIST, BungeeEssentials.ServerList_ALIAS);
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        int online = ProxyServer.getInstance().getOnlineCount() - Messenger.howManyHidden();
        sender.sendMessage(Dictionary.format(Dictionary.LIST_HEADER, "COUNT", String.valueOf(online)));
        for (final ServerInfo info : ProxyServer.getInstance().getServers().values()) {
            if (sender.hasPermission(Permissions.General.LIST_OFFLINE)) {
                print(sender, info);
            } else {
                info.ping(new Callback<ServerPing>() {
                    @Override
                    public void done(ServerPing serverPing, Throwable throwable) {
                        if (throwable == null) {
                            print(sender, info);
                        }
                    }
                });
            }
        }
    }

    private int getNonHiddenPlayers(ServerInfo info) {
        int result = 0;
        for (ProxiedPlayer player : info.getPlayers()) {
            if (!Messenger.isHidden(player)) {
                result++;
            }
        }
        return result;
    }

    private String getDensity(int players) {
        return String.valueOf(getColour(players)) + "(" + players + ")";
    }

    private ChatColor getColour(int players) {
        if (players == 0 || players < 0) {
            return ChatColor.RED;
        }

        int total = ProxyServer.getInstance().getOnlineCount() - Messenger.howManyHidden();
        double percent = (players * 100.0f) / total;
        if (percent <= 33) {
            return ChatColor.RED;
        } else if (percent > 33 && percent <= 66) {
            return ChatColor.GOLD;
        } else {
            return ChatColor.GREEN;
        }
    }

    private void print(CommandSender sender, ServerInfo info) {
        if (info.canAccess(sender) || sender.hasPermission(Permissions.General.LIST_RESTRICTED)) {
            int online = getNonHiddenPlayers(info);
            sender.sendMessage(Dictionary.format(Dictionary.LIST_BODY, "SERVER", info.getName(), "MOTD", info.getMotd(), "DENSITY", getDensity(online), "COUNT", String.valueOf(online)));
        }
    }
}
