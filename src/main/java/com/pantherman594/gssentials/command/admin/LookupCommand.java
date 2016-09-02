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

import com.pantherman594.gssentials.Dictionary;
import com.pantherman594.gssentials.Permissions;
import com.pantherman594.gssentials.command.ServerSpecificCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class LookupCommand extends ServerSpecificCommand {
    public LookupCommand() {
        super("lookup", Permissions.Admin.LOOKUP);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Set<String> matches = new HashSet<>();
        if (args.length == 1 && sender.hasPermission(Permissions.Admin.LOOKUP_INFO)) {
            String uuid = null;
            for (Object nameO : pD.listAllData("lastname")) {
                String name = (String) nameO;
                if (name.equalsIgnoreCase(args[0])) {
                    sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_PLAYER_HEADER, "PLAYER", name));
                    uuid = (String) pD.getData("lastname", name, "uuid");
                    break;
                }
            }

            if (uuid == null) {
                ProxyServer.getInstance().getPluginManager().dispatchCommand(sender, getName() + " -a " + args[0]);
                return;
            }

            sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_PLAYER_FORMAT, "TYPE", "UUID", "INFO", uuid));
            if (sender.hasPermission(Permissions.Admin.LOOKUP_IP) || sender.hasPermission(Permissions.Admin.LOOKUP_ALL))
                sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_PLAYER_FORMAT, "TYPE", "IP", "INFO", pD.getIp(uuid)));
            sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_PLAYER_FORMAT, "TYPE", "Messaging", "INFO", Dictionary.capitalizeFirst(pD.isMsging(uuid) + "")));
            sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_PLAYER_FORMAT, "TYPE", "Muted", "INFO", Dictionary.capitalizeFirst(pD.isMuted(uuid) + "")));
            if (sender.hasPermission(Permissions.Admin.LOOKUP_HIDDEN) || sender.hasPermission(Permissions.Admin.LOOKUP_ALL))
                sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_PLAYER_FORMAT, "TYPE", "Hidden", "INFO", Dictionary.capitalizeFirst(pD.isHidden(uuid) + "")));
            if (sender.hasPermission(Permissions.Admin.LOOKUP_SPY) || sender.hasPermission(Permissions.Admin.LOOKUP_ALL)) {
                sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_PLAYER_FORMAT, "TYPE", "Spy", "INFO", Dictionary.capitalizeFirst(pD.isSpy(uuid) + "")));
                sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_PLAYER_FORMAT, "TYPE", "Command Spy", "INFO", Dictionary.capitalizeFirst(pD.isCSpy(uuid) + "")));
            }
            String list = Dictionary.combine(", ", pD.getFriends(uuid));
            sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_PLAYER_FORMAT, "TYPE", "Friends", "INFO", list.equals("") ? "None" : list));
            list = Dictionary.combine(", ", pD.getOutRequests(uuid));
            sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_PLAYER_FORMAT, "TYPE", "Outgoing Friend Requests", "INFO", list.equals("") ? "None" : list));
            list = Dictionary.combine(", ", pD.getInRequests(uuid));
            sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_PLAYER_FORMAT, "TYPE", "Incoming Friend Requests", "INFO", list.equals("") ? "None" : list));
        } else if (args.length == 2) {
            boolean error = true;
            String partialPlayerName = args[0].toLowerCase();
            int arg = 0;
            String[] possibleArgs = new String[]{"b", "m", "e", "a", "ip"};
            for (String a : possibleArgs) {
                if (args[0].equals("-" + a)) {
                    partialPlayerName = args[1].toLowerCase();
                    error = false;
                } else if (args[1].equals("-" + a)) {
                    arg = 1;
                    error = false;
                }
            }
            if (error) {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", "/" + getName() + " <part of name> [-b|-m|-e|-a|-ip]"));
            } else if (args[arg].equals("-i")) {
                matches.addAll(pD.getDataMultiple("ip", partialPlayerName, "lastname").stream().map(name -> (String) name).collect(Collectors.toList()));
            } else {
                for (Object pO : pD.listAllData("lastname")) {
                    String p = (String) pO;
                    switch (args[arg]) {
                        case "-m":
                            if (p.toLowerCase().substring(1, p.length() - 1).contains(partialPlayerName.toLowerCase())) {
                                matches.add(p);
                            }
                            break;
                        case "-e":
                            if (p.toLowerCase().endsWith(partialPlayerName.toLowerCase())) {
                                matches.add(p);
                            }
                            break;
                        case "-b":
                            if (p.toLowerCase().startsWith(partialPlayerName.toLowerCase())) {
                                matches.add(p);
                            }
                            break;
                        case "-a":
                            if (p.toLowerCase().contains(partialPlayerName.toLowerCase())) {
                                matches.add(p);
                            }
                            break;
                        default:
                            break;
                    }
                }
                sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_HEADER, "SIZE", String.valueOf(matches.size())));
                for (String match : matches) {
                    sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_BODY, "PLAYER", match));
                }
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", "/" + getName() + " <part of name|ip> <-b|-m|-e|-a|-ip>"));
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", "/" + getName() + " <full name>"));
        }
    }
}
