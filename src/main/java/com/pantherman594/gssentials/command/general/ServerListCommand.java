/*
 * BungeeEssentials: Full customization of a few necessary features for your server!
 * Copyright (C) 2015  David Shen (PantherMan594)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pantherman594.gssentials.command.general;

import com.pantherman594.gssentials.command.BECommand;
import com.pantherman594.gssentials.utils.Dictionary;
import com.pantherman594.gssentials.utils.Messenger;
import com.pantherman594.gssentials.utils.Permissions;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@SuppressWarnings("deprecation")
public class ServerListCommand extends BECommand {

    public ServerListCommand() {
        super("list", Permissions.General.LIST);
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
