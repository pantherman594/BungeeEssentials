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

import com.pantherman594.gssentials.*;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class GlobalChatEvent extends Event implements Cancellable {
    private String server;
    private String sender;
    private String msg;
    private boolean cancelled;

    /**
     * The Global Chat event.
     *
     * @param server The server that the chat sender is on.
     * @param sender The chat sender's name.
     * @param msgPre The message before formatting/filtering.
     */
    public GlobalChatEvent(String server, String sender, String msgPre) {
        this.server = server;
        this.sender = sender;
        this.msg = msgPre;

        if (msgPre != null) {
            msgPre = Messenger.filter(ProxyServer.getInstance().getPlayer(sender), msgPre, Messenger.ChatType.GLOBAL);
            TextComponent msg = Dictionary.formatMsg(Dictionary.FORMAT_GCHAT, "SERVER", server, "SENDER", sender, "MESSAGE", msgPre);
            ProxiedPlayer senderP = ProxyServer.getInstance().getPlayer(sender);
            ProxyServer.getInstance().getPlayers().stream().filter(player -> (player.hasPermission(Permissions.General.CHAT + "." + server) || player.hasPermission(Permissions.General.CHAT)) && (senderP == null || !BungeeEssentials.getInstance().contains("ignore") || !PlayerData.getData(player.getUniqueId()).isIgnored(senderP.getUniqueId().toString()))).forEach(player -> player.sendMessage(msg));
            if (msg != null) {
                ProxyServer.getInstance().getConsole().sendMessage(msg);
                Log.log("[GCHAT] " + msg.toLegacyText());
            }
        }
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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

    public String toString() {
        return "ChatEvent(super=" + super.toString() + ", cancelled=" + this.isCancelled() + ", server=" + this.getServer() + ", sender=" + this.getSender() + ", message=" + this.getMessage() + ")";
    }
}
