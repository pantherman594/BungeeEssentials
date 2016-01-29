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

import com.google.common.collect.ImmutableSet;
import com.pantherman594.gssentials.Dictionary;
import com.pantherman594.gssentials.Permissions;
import com.pantherman594.gssentials.PlayerData;
import com.pantherman594.gssentials.command.BECommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public class SlapCommand extends BECommand implements TabExecutor {
    public SlapCommand() {
        super("slap", "");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(Permissions.General.SLAP)) {
            ProxiedPlayer player = null;
            if (sender instanceof ProxiedPlayer) {
                player = (ProxiedPlayer) sender;
            }
            if (args.length > 0) {
                ProxiedPlayer enemy = ProxyServer.getInstance().getPlayer(args[0]);
                if (enemy != null && !PlayerData.getData(enemy.getUniqueId()).isHidden()) {
                    sender.sendMessage(Dictionary.format(Dictionary.SLAPPER_MSG, "SLAPPED", enemy.getName()));
                    enemy.sendMessage(Dictionary.format(Dictionary.SLAPPED_MSG, "SLAPPER", player == null ? "GOD" : player.getName()));
                } else {
                    sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_OFFLINE));
                }
            } else {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", getName() + " <player>"));
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_UNWORTHY_OF_SLAP));
        }
    }


    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length > 1 || args.length == 0) {
            return ImmutableSet.of();
        }

        Set<String> matches = new HashSet<>();
        String search = args[0].toLowerCase();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (!player.getName().equals(sender.getName())) {
                if (player.getName().toLowerCase().startsWith(search) && !PlayerData.getData(player.getUniqueId()).isHidden()) {
                    matches.add(player.getName());
                }
            }
        }
        return matches;
    }
}
