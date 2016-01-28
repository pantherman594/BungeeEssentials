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

package com.pantherman594.gssentials.command.admin;

import com.google.common.collect.ImmutableSet;
import com.pantherman594.gssentials.BungeeEssentials;
import com.pantherman594.gssentials.Dictionary;
import com.pantherman594.gssentials.Permissions;
import com.pantherman594.gssentials.PlayerData;
import com.pantherman594.gssentials.command.ServerSpecificCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public class SendCommand extends ServerSpecificCommand implements TabExecutor {
    public SendCommand() {
        super(BungeeEssentials.getInstance().getMain("send"), Permissions.Admin.SEND);
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeEssentials.getInstance(), new SendAllCommand());
    }

    @Override
    public void run(final CommandSender sender, final String[] args) {
        if (args.length > 1) {
            final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
            if (player != null) {
                sender.sendMessage(Dictionary.format(Dictionary.FORMAT_SEND_PLAYER, "PLAYER", args[0], "SERVER", args[1]));
                final ServerInfo info = ProxyServer.getInstance().getServerInfo(args[1]);
                player.connect(info, (success, throwable) -> {
                    if (!success) {
                        sender.sendMessage(Dictionary.format(Dictionary.ERROR_SENDFAIL, "PLAYER", player.getName(), "SERVER", info.getName()));
                    }
                });
            } else {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_OFFLINE));
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", getName() + " <player> <server>"));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length > 2 || args.length == 0) {
            return ImmutableSet.of();
        }

        Set<String> matches = new HashSet<>();
        String search;
        if (args.length == 1) {
            search = args[0].toLowerCase();
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (!player.getName().equals(sender.getName())) {
                    if (player.getName().toLowerCase().startsWith(search) && !PlayerData.getData(((ProxiedPlayer) sender).getUniqueId()).isHidden()) {
                        matches.add(player.getName());
                    }
                }
            }
        } else {
            search = args[1].toLowerCase();
            for (String server : ProxyServer.getInstance().getServers().keySet()) {
                if (server.toLowerCase().startsWith(search)) {
                    matches.add(server);
                }
            }
        }
        return matches;
    }
}
