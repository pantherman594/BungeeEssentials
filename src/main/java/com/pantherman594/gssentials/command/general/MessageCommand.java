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

import com.google.common.collect.ImmutableSet;
import com.pantherman594.gssentials.BungeeEssentials;
import com.pantherman594.gssentials.command.BECommand;
import com.pantherman594.gssentials.event.MessageEvent;
import com.pantherman594.gssentials.utils.Dictionary;
import com.pantherman594.gssentials.utils.Permissions;
import com.pantherman594.gssentials.utils.PlayerData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public class MessageCommand extends BECommand implements TabExecutor {
    public MessageCommand() {
        super("message", Permissions.General.MESSAGE);
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeEssentials.getInstance(), new ReplyCommand());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (args.length == 1) {
                PlayerData pD = PlayerData.getData(((ProxiedPlayer) sender).getUniqueId());
                boolean change = true;
                if (args[0].equalsIgnoreCase("toggle")) {
                    pD.setMsging(!pD.isMsging());
                } else if (args[0].equalsIgnoreCase("on")) {
                    pD.setMsging(true);
                } else if (args[0].equalsIgnoreCase("off")) {
                    pD.setMsging(false);
                } else {
                    change = false;
                    sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", getName() + " <player> <message>"));
                    sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", getName() + " <on|off|toggle>"));
                }
                if (change) {
                    PlayerData.setData(((ProxiedPlayer) sender).getUniqueId(), pD);
                    if (pD.isMsging()) {
                        sender.sendMessage(Dictionary.format(Dictionary.MESSAGE_ENABLED));
                    } else {
                        sender.sendMessage(Dictionary.format(Dictionary.MESSAGE_DISABLED));
                    }
                }
            } else if (args.length > 1) {
                ProxiedPlayer recipient = ProxyServer.getInstance().getPlayer(args[0]);
                ProxyServer.getInstance().getPluginManager().callEvent(new MessageEvent(sender, recipient, Dictionary.combine(0, args)));
            } else {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", getName() + " <player> <message>"));
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", getName() + " <on|off|toggle>"));
            }
        } else {
            sender.sendMessage(Dictionary.colour("&cSorry, only players can send messages."));
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
