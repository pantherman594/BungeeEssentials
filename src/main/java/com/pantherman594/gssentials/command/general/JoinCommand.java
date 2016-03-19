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
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

@SuppressWarnings("unused")
public class JoinCommand extends BECommand implements TabExecutor {
    public JoinCommand() {
        super("join", Permissions.General.JOIN);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (args == null || args.length < 1) {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", getName() + " <player>"));
                return;
            }

            ProxiedPlayer player = (ProxiedPlayer) sender;
            ProxiedPlayer join = ProxyServer.getInstance().getPlayer(args[0]);
            PlayerData pDJ = PlayerData.getData(join.getUniqueId());
            if (join == null || pDJ.isHidden()) {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_OFFLINE));
                return;
            }

            if (player.getUniqueId() == join.getUniqueId()) {
                sender.sendMessage(ChatColor.RED + "You cannot join yourself!");
                return;
            }

            ServerInfo info = join.getServer().getInfo();
            if (info.canAccess(player)) {
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "Attempting to join " + join.getName() + "'s server..");
                player.connect(info);
            } else {
                sender.sendMessage(ProxyServer.getInstance().getTranslation("no_server_permission"));
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Console cannot join servers");
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return args.length == 1 ? tabPlayers(sender, args[0]) : ImmutableSet.of();
    }
}
