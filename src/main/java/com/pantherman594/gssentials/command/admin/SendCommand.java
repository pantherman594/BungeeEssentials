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
import com.pantherman594.gssentials.command.ServerSpecificCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

@SuppressWarnings({"unused", "Duplicates"})
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
