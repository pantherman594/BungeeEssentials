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
import com.pantherman594.gssentials.command.ServerSpecificCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

@SuppressWarnings("unused")
public class MuteCommand extends ServerSpecificCommand implements TabExecutor {
    public MuteCommand() {
        super("mute", Permissions.Admin.MUTE);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args != null && args.length > 0) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
            if (player != null) {
                String uuid = player.getUniqueId().toString();
                if (!player.hasPermission(Permissions.Admin.MUTE_EXEMPT)) {
                    if (pD.toggleMuted(uuid)) {
                        player.sendMessage(Dictionary.format(Dictionary.MUTE_ENABLED));
                        ProxyServer.getInstance().getPlayers().stream().filter(p -> p.hasPermission(Permissions.Admin.NOTIFY)).forEach(p -> sender.sendMessage(Dictionary.format(Dictionary.MUTE_ENABLEDN, "PLAYER", player.getName())));
                    } else {
                        player.sendMessage(Dictionary.format(Dictionary.MUTE_DISABLED));
                        ProxyServer.getInstance().getPlayers().stream().filter(p -> p.hasPermission(Permissions.Admin.NOTIFY)).forEach(p -> sender.sendMessage(Dictionary.format(Dictionary.MUTE_DISABLEDN, "PLAYER", player.getName())));
                    }
                } else {
                    sender.sendMessage(Dictionary.format(Dictionary.MUTE_EXEMPT));
                }
            } else {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_NOT_FOUND));
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", "/" + getName() + " <player>"));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return args.length == 1 ? tabPlayers(sender, args[0]) : ImmutableSet.of();
    }
}
