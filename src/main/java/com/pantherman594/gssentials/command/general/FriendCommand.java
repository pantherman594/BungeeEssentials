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
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Set;
import java.util.UUID;

/**
 * Created by David on 12/05.
 *
 * @author David
 */
@SuppressWarnings("unused")
public class FriendCommand extends BECommand implements TabExecutor {
    public FriendCommand() {
        super("friend", Permissions.General.FRIEND);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            String uuid = ((ProxiedPlayer) sender).getUniqueId().toString();
            Set<String> friends = pD.getFriends(uuid);
            if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("list"))) {
                sender.sendMessage(Dictionary.format(Dictionary.FRIEND_HEADER, "COUNT", String.valueOf(pD.getFriends(uuid).size())));

                for (String friend : friends) {
                    String server = "Offline";
                    String name;

                    if (ProxyServer.getInstance().getPlayer(UUID.fromString(friend)) != null) {
                        server = ProxyServer.getInstance().getPlayer(UUID.fromString(friend)).getServer().getInfo().getName();
                        name = ProxyServer.getInstance().getPlayer(UUID.fromString(friend)).getName();
                    } else {
                        name = pD.getName(uuid);
                    }

                    sender.sendMessage(Dictionary.format(Dictionary.FRIEND_BODY, "NAME", name, "SERVER", server));
                }

                boolean headerSent = false;

                Set<String> outRequests = pD.getOutRequests(uuid);
                for (String outRequest : outRequests) {
                    if (!headerSent) {
                        sender.sendMessage(Dictionary.format(Dictionary.OUTREQUESTS_HEADER, "COUNT", String.valueOf(outRequests.size())));
                        headerSent = true;
                    }

                    String name;

                    if (ProxyServer.getInstance().getPlayer(UUID.fromString(outRequest)) != null) {
                        name = ProxyServer.getInstance().getPlayer(UUID.fromString(outRequest)).getName();
                    } else {
                        name = pD.getName(outRequest);
                    }

                    sender.sendMessage(Dictionary.format(Dictionary.OUTREQUESTS_BODY, "NAME", name));
                }

                headerSent = false;

                Set<String> inRequests = pD.getInRequests(uuid);
                for (String inRequest : inRequests) {
                    if (!headerSent) {
                        sender.sendMessage(Dictionary.format(Dictionary.INREQUESTS_HEADER, "COUNT", String.valueOf(inRequests.size())));
                        headerSent = true;
                    }

                    String name;

                    if (ProxyServer.getInstance().getPlayer(UUID.fromString(inRequest)) != null) {
                        name = ProxyServer.getInstance().getPlayer(UUID.fromString(inRequest)).getName();
                    } else {
                        name = pD.getName(inRequest);
                    }

                    sender.sendMessage(Dictionary.format(Dictionary.INREQUESTS_BODY, "NAME", name));
                }
            } else if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("deny"))) {
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(args[1]);
                String friendUuid;

                if (p == sender) {
                    sender.sendMessage(Dictionary.format("&cYou can't be friends with yourself!."));
                } else {

                    if (p != null) { // If the player is online, pull up the player's data.
                        friendUuid = p.getUniqueId().toString();
                    } else { // If the player is offline, lookup the player's uuid and load it.
                        friendUuid = BungeeEssentials.getInstance().getOfflineUUID(args[1]);
                        if (friendUuid == null) {
                            sender.sendMessage(Dictionary.format("&cError: Could not find player."));
                            return;
                        }
                    }

                    String friendName = pD.getName(friendUuid);

                    if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("accept")) {
                        if (pD.getFriends(uuid).contains(friendUuid)) { // Tell player if they are already friends
                            sender.sendMessage(Dictionary.format(Dictionary.FRIEND_OLD, "NAME", args[1]));
                        } else {
                            if (p != null && !pD.isHidden(friendUuid)) { // Will only add if player is online.

                                if (pD.getInRequests(uuid).contains(friendUuid)) {
                                /*
                                 * If the command sender already has an incoming request from the target player, will treat as accepting the request.
                                 * Will add as a friend on both sides and remove the requests.
                                 */
                                    pD.removeInRequest(uuid, friendUuid);
                                    pD.addFriend(uuid, friendUuid);

                                    pD.removeOutRequest(friendUuid, uuid);
                                    pD.addFriend(friendUuid, uuid);

                                    p.sendMessage(Dictionary.format(Dictionary.FRIEND_NEW, "NAME", sender.getName()));
                                    sender.sendMessage(Dictionary.format(Dictionary.FRIEND_NEW, "NAME", p.getName()));

                                } else if (!pD.getOutRequests(uuid).contains(friendUuid)) {
                                    // If not sender's outgoing requests already, will add. Will also add to target's incoming requests.
                                    pD.addOutRequest(uuid, friendUuid);
                                    pD.addInRequest(friendUuid, uuid);

                                    p.sendMessage(Dictionary.format(Dictionary.INREQUESTS_NEW, "NAME", sender.getName()));
                                    sender.sendMessage(Dictionary.format(Dictionary.OUTREQUESTS_NEW, "NAME", p.getName()));
                                } else {
                                    // If none of the above true, means sender has already sent a request.
                                    sender.sendMessage(Dictionary.format(Dictionary.OUTREQUESTS_OLD, "NAME", p.getName()));
                                }
                            } else {
                                sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_NOT_FOUND));
                            }
                        }
                    } else { // For denying/removing
                        if (pD.getFriends(uuid).contains(friendUuid)) { // If they are currently friends, will remove from both.
                            pD.removeFriend(uuid, friendUuid);
                            pD.removeFriend(friendUuid, uuid);

                            sender.sendMessage(Dictionary.format(Dictionary.FRIEND_REMOVE, "NAME", friendName));
                            if (p != null) {
                                p.sendMessage(Dictionary.format(Dictionary.FRIEND_REMOVE, "NAME", sender.getName()));
                            }

                        } else if (pD.getOutRequests(uuid).contains(friendUuid)) { // If sender has a pending outgoing request, will remove that and the target's incoming request.
                            pD.removeOutRequest(uuid, friendUuid);
                            pD.removeInRequest(friendUuid, uuid);

                            sender.sendMessage(Dictionary.format(Dictionary.OUTREQUESTS_REMOVE, "NAME", friendName));
                            if (p != null) {
                                p.sendMessage(Dictionary.format(Dictionary.INREQUESTS_REMOVE, "NAME", sender.getName()));
                            }

                        } else if (pD.getInRequests(uuid).contains(friendUuid)) { // If sender has incoming request, will treat as denying request.
                            pD.removeInRequest(uuid, friendUuid);
                            pD.removeOutRequest(friendUuid, uuid);

                            sender.sendMessage(Dictionary.format(Dictionary.INREQUESTS_REMOVE, "NAME", friendName));
                            if (p != null) {
                                p.sendMessage(Dictionary.format(Dictionary.OUTREQUESTS_REMOVE, "NAME", sender.getName()));
                            }

                        } else {
                            sender.sendMessage(Dictionary.format(Dictionary.CANNOT_REMOVE_FRIEND, "NAME", friendName));
                        }
                    }
                }
            } else {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", "/" + getName() + " [list|add <player>|remove <player>]"));
            }
        } else {
            sender.sendMessage(Dictionary.format("&cConsole does not have any friends."));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        switch (args.length) {
            case 2:
                return tabPlayers(sender, args[1]);
            case 1:
                return tabStrings(args[0], new String[]{"list", "add", "remove"});
            default:
                return ImmutableSet.of();
        }
    }
}
