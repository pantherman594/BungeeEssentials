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

package com.pantherman594.gssentials.event;

import com.pantherman594.gssentials.BungeeEssentials;
import com.pantherman594.gssentials.utils.Dictionary;
import com.pantherman594.gssentials.utils.Messenger;
import com.pantherman594.gssentials.utils.Permissions;
import com.pantherman594.gssentials.utils.PlayerData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

import java.util.concurrent.TimeUnit;

public class MessageEvent extends Event {
    private CommandSender sender;
    private ProxiedPlayer recipient;
    private String msg;

    public MessageEvent(CommandSender sender, ProxiedPlayer recipient, String msg) {
        this.sender = sender;
        this.recipient = recipient;
        this.msg = msg;
        String message = msg;
        if (recipient != null && !PlayerData.getData(recipient.getUniqueId()).isHidden()) {
            ProxiedPlayer player = null;
            if (sender instanceof ProxiedPlayer) {
                player = (ProxiedPlayer) sender;
                message = Messenger.filter(player, msg, Messenger.ChatType.PRIVATE);
            }
            String server = player != null ? player.getServer().getInfo().getName() : "CONSOLE";
            if (player != null) {

                if (!sender.hasPermission(Permissions.Admin.SPY_EXEMPT)) {
                    String spyMessage = Dictionary.format(Dictionary.SPY_MESSAGE, "SERVER", server, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message);
                    for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                        if (player.getUniqueId() != onlinePlayer.getUniqueId() && recipient.getUniqueId() != onlinePlayer.getUniqueId()) {
                            if (onlinePlayer.hasPermission(Permissions.Admin.SPY) && PlayerData.getData(onlinePlayer.getUniqueId()).isSpy()) {
                                onlinePlayer.sendMessage(spyMessage);
                            }
                        }
                    }
                }
                final ProxiedPlayer recp = recipient;
                final ProxiedPlayer play = player;
                Messenger.messages.put(play.getUniqueId(), recp.getUniqueId());
                ProxyServer.getInstance().getScheduler().schedule(BungeeEssentials.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        Messenger.messages.put(recp.getUniqueId(), play.getUniqueId());
                    }
                }, 3, TimeUnit.SECONDS);
            }
            PlayerData pDR = PlayerData.getData(recipient.getUniqueId());
            if (BungeeEssentials.getInstance().contains("ignore")) {
                PlayerData pDS = PlayerData.getData(((ProxiedPlayer) sender).getUniqueId());
                if (!pDS.isIgnored(recipient.getUniqueId())) {
                    if (!pDR.isIgnored(((ProxiedPlayer) sender).getUniqueId()) && (pDR.isMsging() || sender.hasPermission(Permissions.Admin.BYPASS_MSG))) {
                        recipient.sendMessage(Dictionary.formatMsg(Dictionary.MESSAGE_FORMAT, "SERVER", server, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));
                    }
                    sender.sendMessage(Dictionary.formatMsg(Dictionary.MESSAGE_FORMAT, "SERVER", recipient.getServer().getInfo().getName(), "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));
                } else {
                    sender.sendMessage(Dictionary.format(Dictionary.ERROR_IGNORING));
                }
            } else {
                sender.sendMessage(Dictionary.formatMsg(Dictionary.MESSAGE_FORMAT, "SERVER", recipient.getServer().getInfo().getName(), "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));
                if (pDR.isMsging() || sender.hasPermission(Permissions.Admin.BYPASS_MSG)) {
                    recipient.sendMessage(Dictionary.formatMsg(Dictionary.MESSAGE_FORMAT, "SERVER", server, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));
                }
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_OFFLINE));
        }
    }

    public CommandSender getSender() {
        return sender;
    }

    public ProxiedPlayer getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return msg;
    }
}
