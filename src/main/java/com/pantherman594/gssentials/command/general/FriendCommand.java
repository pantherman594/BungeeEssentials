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

import com.pantherman594.gssentials.BungeeEssentials;
import com.pantherman594.gssentials.command.BECommand;
import com.pantherman594.gssentials.utils.Dictionary;
import com.pantherman594.gssentials.utils.Friends;
import com.pantherman594.gssentials.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Created by David on 12/05.
 *
 * @author David
 */
public class FriendCommand extends BECommand {

    public FriendCommand() {
        super("friend", Permissions.General.FRIEND);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            Friends friends = BungeeEssentials.getInstance().getFriends(((ProxiedPlayer) sender).getUniqueId());
            if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("list"))) {
                sender.sendMessage(Dictionary.format(Dictionary.FRIEND_HEADER, "COUNT", String.valueOf(friends.getFriends().size())));
                for (String name : friends.getFriends()) {
                    String server = "Offline";
                    if (ProxyServer.getInstance().getPlayer(name) != null) {
                        server = ProxyServer.getInstance().getPlayer(name).getServer().getInfo().getName();
                    }
                    sender.sendMessage(Dictionary.format(Dictionary.FRIEND_BODY, "NAME", name, "SERVER", server));
                }
                sender.sendMessage(Dictionary.format(Dictionary.OUTREQUESTS_HEADER, "COUNT", String.valueOf(friends.getOutRequests().size())));
                for (String name : friends.getOutRequests()) {
                    sender.sendMessage(Dictionary.format(Dictionary.OUTREQUESTS_BODY, "NAME", name));
                }
                sender.sendMessage(Dictionary.format(Dictionary.INREQUESTS_HEADER, "COUNT", String.valueOf(friends.getInRequests().size())));
                for (String name : friends.getInRequests()) {
                    sender.sendMessage(Dictionary.format(Dictionary.INREQUESTS_BODY, "NAME", name));
                }
            } else if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(args[1]);
                String uuid;
                Friends friends2;
                if (p != null) {
                    uuid = p.getUniqueId().toString();
                    friends2 = BungeeEssentials.getInstance().getFriends(p.getUniqueId());
                } else {
                    uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + args[1]).getBytes(StandardCharsets.UTF_8)).toString();
                    friends2 = new Friends(uuid);
                }
                if (args[0].equalsIgnoreCase("add")) {
                    if (friends.getFriends().contains(uuid)) {
                        sender.sendMessage(Dictionary.format(Dictionary.FRIEND_OLD, "NAME", args[1]));
                    } else {
                        if (p != null) {
                            friends2 = BungeeEssentials.getInstance().getFriends(p.getUniqueId());
                            uuid = p.getUniqueId().toString();
                            if (friends.getInRequests().contains(uuid)) {
                                friends.getInRequests().remove(uuid);
                                friends.getFriends().add(uuid);
                                friends2.getOutRequests().remove(uuid);
                                friends2.getFriends().add(uuid);
                                p.sendMessage(Dictionary.format(Dictionary.FRIEND_NEW, "NAME", sender.getName()));
                                sender.sendMessage(Dictionary.format(Dictionary.FRIEND_NEW, "NAME", p.getName()));
                            }
                            if (!friends.getOutRequests().contains(uuid)) {
                                friends.getOutRequests().add(uuid);
                                p.sendMessage(Dictionary.format(Dictionary.INREQUESTS_NEW, "SENDER", sender.getName(), "NAME", p.getName()));
                                sender.sendMessage(Dictionary.format(Dictionary.OUTREQUESTS_NEW, "NAME", p.getName()));
                            } else {
                                sender.sendMessage(Dictionary.format(Dictionary.OUTREQUESTS_OLD, "NAME", p.getName()));
                            }
                        } else {
                            sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_OFFLINE));
                        }
                    }
                } else {
                    if (friends.getFriends().contains(uuid)) {
                        friends.getFriends().remove(uuid);
                        friends2.getFriends().remove(((ProxiedPlayer) sender).getUniqueId().toString());
                        sender.sendMessage(Dictionary.format(Dictionary.FRIEND_REMOVE, "NAME", p.getName()));
                        if (p != null) {
                            p.sendMessage(Dictionary.format(Dictionary.FRIEND_REMOVE, "NAME", sender.getName()));
                        }
                    } else if (friends.getOutRequests().contains(uuid)) {
                        friends.getOutRequests().remove(uuid);
                        friends2.getInRequests().remove(((ProxiedPlayer) sender).getUniqueId().toString());
                        sender.sendMessage(Dictionary.format(Dictionary.OUTREQUESTS_REMOVE, "NAME", p.getName()));
                        if (p != null) {
                            p.sendMessage(Dictionary.format(Dictionary.INREQUESTS_REMOVE, "NAME", sender.getName()));
                        }
                    } else if (friends.getInRequests().contains(uuid)) {
                        friends.getInRequests().remove(uuid);
                        friends2.getOutRequests().remove(((ProxiedPlayer) sender).getUniqueId().toString());
                        sender.sendMessage(Dictionary.format(Dictionary.INREQUESTS_REMOVE, "NAME", p.getName()));
                        if (p != null) {
                            p.sendMessage(Dictionary.format(Dictionary.OUTREQUESTS_REMOVE, "NAME", sender.getName()));
                        }
                    }
                    if (p != null) {
                        BungeeEssentials.getInstance().setFriends(p.getUniqueId(), friends2);
                    } else {
                        friends2.save();
                    }
                }
            } else {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", getName() + " [list|add <player>|remove <player>"));
            }
        } else {
            sender.sendMessage(Dictionary.colour("&cConsole does not have any friends."));
        }
    }
}
