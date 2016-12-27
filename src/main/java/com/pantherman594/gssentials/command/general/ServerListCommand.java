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

import com.pantherman594.gssentials.BungeeEssentials;
import com.pantherman594.gssentials.Dictionary;
import com.pantherman594.gssentials.Permissions;
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

@SuppressWarnings("unused")
public class ServerListCommand extends BECommand {
    public ServerListCommand() {
        super("list", Permissions.General.LIST);
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        boolean canSeeHidden = Permissions.hasPerm(sender, Permissions.Admin.SEE_HIDDEN);
        int online = BungeeEssentials.getInstance().getMessenger().getVisiblePlayers(canSeeHidden).size();

        String current = "CONSOLE";
        if (sender instanceof ProxiedPlayer) {
            current = ((ProxiedPlayer) sender).getServer().getInfo().getName();
        }

        sender.sendMessage(Dictionary.format(Dictionary.LIST_HEADER, "COUNT", String.valueOf(online), "CURRENT", current));
        for (final ServerInfo info : ProxyServer.getInstance().getServers().values()) {
            try (Socket s = new Socket()) {
                s.connect(info.getAddress());
                print(sender, canSeeHidden, info, false);
            } catch (IOException e) {
                if (Permissions.hasPerm(sender, Permissions.General.LIST_OFFLINE)) {
                    print(sender, canSeeHidden, info, true);
                }
            }
        }
    }

    /**
     * Get a list of players based on the server and visibility.
     *
     * @param canSeeHidden Whether the command sender can see hidden players.
     * @param info         The server to list players from.
     * @return A list of the players on that server.
     */
    private Collection<ProxiedPlayer> getPlayers(boolean canSeeHidden, ServerInfo info) {
        if (canSeeHidden && !info.getPlayers().isEmpty()) {
            return info.getPlayers();
        }
        return info.getPlayers().stream().filter(player -> !pD.isHidden((player).getUniqueId().toString())).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Get the color-coded player count of a server's players relative to the others.
     *
     * @param canSeeHidden Whether the command sender can see hidden players.
     * @param players      The number of players.
     * @return Color-coded number of players based on the density.
     */
    private String getDensity(boolean canSeeHidden, int players) {
        return String.valueOf(getColor(canSeeHidden, players)) + players;
    }

    /**
     * Get the color of a server's density, based on the percentage of all players.
     * Green: More than 66%
     * Gold: Between 33% and 66%
     * Red: Less than 33%
     *
     * @param canSeeHidden Whether the command sender can see hidden players.
     * @param players      The number of players.
     * @return The color for the server's density.
     */
    private ChatColor getColor(boolean canSeeHidden, int players) {
        if (players == 0 || players < 0) {
            return ChatColor.RED;
        }

        int total = BungeeEssentials.getInstance().getMessenger().getVisiblePlayers(canSeeHidden).size();
        double percent = (players * 100.0f) / total;
        if (percent <= 33) {
            return ChatColor.RED;
        } else if (percent > 33 && percent <= 66) {
            return ChatColor.GOLD;
        } else {
            return ChatColor.GREEN;
        }
    }

    /**
     * Get a formatted list of players.
     *
     * @param players A list of the players
     * @return A comma-delimited list of the players
     */
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

    /**
     * Send the completed and formatted list of players to the command sender, per server.
     *
     * @param sender The command sender (receiver of the list).
     * @param canSeeHidden Whether the command sender can see hidden players.
     * @param info The server to list players from.
     * @param offline Whether the server is offline.
     */
    private void print(CommandSender sender, boolean canSeeHidden, ServerInfo info, boolean offline) {
        if (info.canAccess(sender) || Permissions.hasPerm(sender, Permissions.General.LIST_RESTRICTED)) {
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
