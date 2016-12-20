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
import com.pantherman594.gssentials.event.MessageEvent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

@SuppressWarnings("unused")
public class MessageCommand extends BECommand implements TabExecutor {
    public MessageCommand() {
        super("message", Permissions.General.MESSAGE);
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeEssentials.getInstance(), new ReplyCommand());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender instanceof ProxiedPlayer) {
                String uuid = ((ProxiedPlayer) sender).getUniqueId().toString();
                boolean change = true;
                boolean status = false;
                if (args[0].equalsIgnoreCase("toggle")) {
                    status = pD.toggleMsging(uuid);
                } else if (args[0].equalsIgnoreCase("on")) {
                    pD.setMsging(uuid, true);
                    status = true;
                } else if (args[0].equalsIgnoreCase("off")) {
                    pD.setMsging(uuid, false);
                } else {
                    change = false;
                    sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP",
                            "\n  /" + getName() + " <player> <message>" +
                                    "\n  /" + getName() + " <on|off|toggle>"));
                }
                if (change) {
                    if (status) {
                        sender.sendMessage(Dictionary.format(Dictionary.MESSAGE_ENABLED));
                    } else {
                        sender.sendMessage(Dictionary.format(Dictionary.MESSAGE_DISABLED));
                    }
                }
            } else {
                sender.sendMessage(Dictionary.format("&cSorry, Console cannot toggle messages."));
            }
        } else if (args.length > 1) {
            if (!args[0].equalsIgnoreCase("CONSOLE")) {
                ProxiedPlayer recipient = ProxyServer.getInstance().getPlayer(args[0]);
                if (recipient == null) {
                    for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                        if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                            recipient = p;
                            break;
                        }
                    }
                }

                if (recipient != null && (!(sender instanceof ProxiedPlayer) || ((ProxiedPlayer) sender).getServer().getInfo() == recipient.getServer().getInfo() || sender.hasPermission(Permissions.General.MESSAGE_GLOBAL))) {
                    ProxyServer.getInstance().getPluginManager().callEvent(new MessageEvent(sender, recipient, Dictionary.combine(0, args)));
                } else {
                    sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_NOT_FOUND));
                }
            } else {
                ProxyServer.getInstance().getPluginManager().callEvent(new MessageEvent(sender, ProxyServer.getInstance().getConsole(), Dictionary.combine(0, args)));
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP",
                    "\n  /" + getName() + " <player> <message>" +
                            "\n  /" + getName() + " <on|off|toggle>"));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return args.length == 1 ? tabPlayers(sender, args[0]) : ImmutableSet.of();
    }
}
