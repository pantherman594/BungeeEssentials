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
import com.pantherman594.gssentials.Dictionary;
import com.pantherman594.gssentials.Permissions;
import com.pantherman594.gssentials.command.BECommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Collection;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public class SendAllCommand extends BECommand implements TabExecutor {
    public SendAllCommand() {
        super("sendall", Permissions.Admin.SENDALL);
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        if (args.length > 0) {
            ServerInfo sInfo = ProxyServer.getInstance().getServerInfo(args[0]);
            Collection<ProxiedPlayer> players = ProxyServer.getInstance().getPlayers();
            if (args.length > 1) {
                sInfo = ProxyServer.getInstance().getServerInfo(args[1]);
                players = ProxyServer.getInstance().getServerInfo(args[0]).getPlayers();
            }
            final ServerInfo info = sInfo;
            for (final ProxiedPlayer player : players) {
                player.connect(info, (success, throwable) -> {
                    if (!success) {
                        sender.sendMessage(Dictionary.format(Dictionary.ERROR_SENDFAIL, "PLAYER", player.getName(), "SERVER", info.getName()));
                    }
                });
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", getName() + " [fromServer] <toServer>"));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return tabPlayers(sender, args[0]);
            case 2:
                return tabStrings(args[1], ProxyServer.getInstance().getServers().keySet().toArray(new String[ProxyServer.getInstance().getServers().keySet().size()]));
            default:
                return ImmutableSet.of();
        }
    }
}
