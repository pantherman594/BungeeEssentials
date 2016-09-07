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

package com.pantherman594.gssentials.event;

import com.pantherman594.gssentials.BungeeEssentials;
import com.pantherman594.gssentials.Dictionary;
import com.pantherman594.gssentials.Messenger;
import com.pantherman594.gssentials.Permissions;
import com.pantherman594.gssentials.database.PlayerData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

import java.util.concurrent.TimeUnit;

public class MessageEvent extends Event implements Cancellable {
    private CommandSender sender;
    private CommandSender recipient;
    private String msg;
    private boolean cancelled;

    /**
     * The Message event.
     * This uses a slightly modified filtering system.
     *
     * @param sender    The message sender.
     * @param recipient The recipient of the message.
     * @param msg       The message before formatting/filtering.
     */
    public MessageEvent(CommandSender sender, CommandSender recipient, String msg) {
        this.sender = sender;
        this.recipient = recipient;
        this.msg = msg;
        String message = msg;
        PlayerData pD = BungeeEssentials.getInstance().getPlayerData();
        if (recipient != null && recipient instanceof ProxiedPlayer && !pD.isHidden(((ProxiedPlayer) recipient).getUniqueId().toString())) {
            ProxiedPlayer player = null;
            if (sender instanceof ProxiedPlayer) {
                player = (ProxiedPlayer) sender;
                message = BungeeEssentials.getInstance().getMessenger().filter(player, msg, Messenger.ChatType.PRIVATE);
            }
            String server = player != null ? player.getServer().getInfo().getName() : "CONSOLE";
            if (player != null) {

                if (!sender.hasPermission(Permissions.Admin.SPY_EXEMPT)) {
                    TextComponent spyMessage = Dictionary.format(Dictionary.SPY_MESSAGE, "SERVER", server, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message);
                    for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                        if (player.getUniqueId() != onlinePlayer.getUniqueId() && ((ProxiedPlayer) recipient).getUniqueId() != onlinePlayer.getUniqueId()) {
                            if (onlinePlayer.hasPermission(Permissions.Admin.SPY) && pD.isSpy(onlinePlayer.getUniqueId().toString())) {
                                onlinePlayer.sendMessage(spyMessage);
                            }
                        }
                    }
                    if (pD.isSpy("CONSOLE") && spyMessage != null) {
                        ProxyServer.getInstance().getConsole().sendMessage(spyMessage);
                    }
                }
                final ProxiedPlayer recp = (ProxiedPlayer) recipient;
                final ProxiedPlayer play = player;
                BungeeEssentials.getInstance().getMessenger().messages.put(play.getUniqueId(), recp.getUniqueId());
                if (pD.isMsging(recp.getUniqueId().toString())) {
                    ProxyServer.getInstance().getScheduler().schedule(BungeeEssentials.getInstance(), () -> BungeeEssentials.getInstance().getMessenger().messages.put(recp.getUniqueId(), play.getUniqueId()), 3, TimeUnit.SECONDS);
                }
            }
            String uuidR = ((ProxiedPlayer) recipient).getUniqueId().toString();
            if (sender != ProxyServer.getInstance().getConsole() && BungeeEssentials.getInstance().contains("ignore")) {
                String uuidS = ((ProxiedPlayer) sender).getUniqueId().toString();
                if (message != null) {
                    if (!pD.isIgnored(uuidS, uuidR)) {
                        if (!pD.isIgnored(uuidR, uuidS) && (pD.isMsging(uuidR) || sender.hasPermission(Permissions.Admin.BYPASS_MSG))) {
                            recipient.sendMessage(Dictionary.formatMsg(Dictionary.MESSAGE_FORMAT_RECEIVE, "SERVER", server, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));
                        }
                        sender.sendMessage(Dictionary.formatMsg(Dictionary.MESSAGE_FORMAT_SEND, "SERVER", ((ProxiedPlayer) recipient).getServer().getInfo().getName(), "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));
                    } else {
                        sender.sendMessage(Dictionary.format(Dictionary.ERROR_IGNORING));
                    }
                }
            } else {
                sender.sendMessage(Dictionary.formatMsg(Dictionary.MESSAGE_FORMAT_SEND, "SERVER", ((ProxiedPlayer) recipient).getServer().getInfo().getName(), "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));
                if (message != null && pD.isMsging(uuidR) || sender.hasPermission(Permissions.Admin.BYPASS_MSG)) {
                    recipient.sendMessage(Dictionary.formatMsg(Dictionary.MESSAGE_FORMAT_RECEIVE, "SERVER", server, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));
                }
            }
        } else if (recipient == ProxyServer.getInstance().getConsole()) {
            String server = sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getServer().getInfo().getName() : "CONSOLE";
            String serverC = recipient instanceof ProxiedPlayer ? ((ProxiedPlayer) recipient).getServer().getInfo().getName() : "CONSOLE";
            if (message != null) {
                recipient.sendMessage(Dictionary.formatMsg(Dictionary.MESSAGE_FORMAT_RECEIVE, "SERVER", server, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));
                sender.sendMessage(Dictionary.formatMsg(Dictionary.MESSAGE_FORMAT_SEND, "SERVER", serverC, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_NOT_FOUND));
        }
    }

    public CommandSender getSender() {
        return sender;
    }

    public void setSender(CommandSender sender) {
        this.sender = sender;
    }

    public CommandSender getRecipient() {
        return recipient;
    }

    public void setRecipient(ProxiedPlayer recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
