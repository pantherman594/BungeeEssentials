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
import com.pantherman594.gssentials.BungeeEssentials;
import com.pantherman594.gssentials.Dictionary;
import com.pantherman594.gssentials.Permissions;
import com.pantherman594.gssentials.command.BECommand;
import com.pantherman594.gssentials.database.PlayerData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

@SuppressWarnings("unused")
public class FindCommand extends BECommand implements TabExecutor {
    private PlayerData pD = BungeeEssentials.getInstance().getPlayerData();

    public FindCommand() {
        super("find", Permissions.General.FIND);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_NOT_FOUND));
            } else {
                String uuid = player.getUniqueId().toString();

                if (!pD.isHidden(uuid)) {
                    sender.sendMessage(Dictionary.format(Dictionary.FORMAT_FIND_PLAYER, "SERVER", player.getServer().getInfo().getName(), "PLAYER", player.getName()));
                } else {
                    sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_NOT_FOUND));
                }
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", getName() + " <player>"));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return args.length == 1 ? tabPlayers(sender, args[0]) : ImmutableSet.of();
    }
}
