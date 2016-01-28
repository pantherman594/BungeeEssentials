/*
 * BungeeEssentials: Full customization of a few necessary features for your server!
 * Copyright (C) 2016 David Shen (PantherMan594)
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

import com.pantherman594.gssentials.Dictionary;
import com.pantherman594.gssentials.Messenger;
import com.pantherman594.gssentials.Permissions;
import com.pantherman594.gssentials.PlayerData;
import com.pantherman594.gssentials.command.BECommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class ServerListCommand extends BECommand {

    public ServerListCommand() {
        super("list", Permissions.General.LIST);
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        boolean canSeeHidden = sender.hasPermission(Permissions.Admin.SEE_HIDDEN);
        int online = ProxyServer.getInstance().getOnlineCount();
        if (!canSeeHidden) {
            online = online - Messenger.hiddenNum();
        }
        sender.sendMessage(Dictionary.format(Dictionary.LIST_HEADER, "COUNT", String.valueOf(online)));
        for (final ServerInfo info : ProxyServer.getInstance().getServers().values()) {
            try {
                Socket s = new Socket();
                s.connect(info.getAddress());
                s.close();
                print(sender, canSeeHidden, info, false);
            } catch (IOException ignored) {
                if (sender.hasPermission(Permissions.General.LIST_OFFLINE)) {
                    print(sender, canSeeHidden, info, true);
                }
            }
        }
    }

    private Collection<ProxiedPlayer> getPlayers(boolean canSeeHidden, ServerInfo info) {
        if (canSeeHidden && !info.getPlayers().isEmpty()) {
            return info.getPlayers();
        }
        return info.getPlayers().stream().filter(player -> !PlayerData.getData((player).getUniqueId()).isHidden()).collect(Collectors.toCollection(ArrayList::new));
    }

    private String getDensity(boolean canSeeHidden, int players) {
        return String.valueOf(getColour(canSeeHidden, players)) + players;
    }

    private ChatColor getColour(boolean canSeeHidden, int players) {
        if (players == 0 || players < 0) {
            return ChatColor.RED;
        }

        int total = ProxyServer.getInstance().getOnlineCount();
        if (!canSeeHidden) {
            total = total - Messenger.hiddenNum();
        }
        double percent = (players * 100.0f) / total;
        if (percent <= 33) {
            return ChatColor.RED;
        } else if (percent > 33 && percent <= 66) {
            return ChatColor.GOLD;
        } else {
            return ChatColor.GREEN;
        }
    }

    private String getPlayerList(Collection<ProxiedPlayer> players) {
        StringBuilder pList = new StringBuilder();
        for (ProxiedPlayer p : players) {
            if (pList.length() > 0) {
                pList.append(", ");
            }
            pList.append(p.getName());
        }
        return pList.toString();
    }

    private void print(CommandSender sender, boolean canSeeHidden, ServerInfo info, boolean offline) {
        if (info.canAccess(sender) || sender.hasPermission(Permissions.General.LIST_RESTRICTED)) {
            if (offline) {
                sender.sendMessage(Dictionary.format(Dictionary.LIST_BODY, "SERVER", info.getName(), "MOTD", info.getMotd(), "DENSITY", "Offline", "COUNT", "Offline", "PLAYERS", ""));
            } else {
                Collection<ProxiedPlayer> online;
                online = getPlayers(canSeeHidden, info);
                sender.sendMessage(Dictionary.format(Dictionary.LIST_BODY, "SERVER", info.getName(), "MOTD", info.getMotd(), "DENSITY", getDensity(canSeeHidden, online.size()), "COUNT", String.valueOf(online.size()), "PLAYERS", getPlayerList(online)));
            }
        }
    }
}
