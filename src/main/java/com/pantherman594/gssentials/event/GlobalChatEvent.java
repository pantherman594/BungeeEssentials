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

public class GlobalChatEvent extends BEChatEvent implements Cancellable {

    /**
     * The Global Chat event.
     *
     * @param server The server that the chat sender is on.
     * @param sender The chat sender's name.
     * @param msgPre The message before formatting/filtering.
     */
    public GlobalChatEvent(String server, String sender, String msgPre) {
        super(server, sender, msgPre);
    }

    public void execute() {
        String msgPre = getMessage();
        if (msgPre != null) {
            msgPre = BungeeEssentials.getInstance().getMessenger().filter(ProxyServer.getInstance().getPlayer(getSender()), msgPre, Messenger.ChatType.GLOBAL);
            if (msgPre != null) {
                TextComponent msg = Dictionary.formatMsg(Dictionary.FORMAT_GCHAT, "SERVER", getServer(), "SENDER", getSender(), "MESSAGE", msgPre);
                ProxiedPlayer senderP = ProxyServer.getInstance().getPlayer(getSender());
                ProxyServer.getInstance().getPlayers().stream().filter(player -> (Permissions.hasPerm(player, Permissions.General.CHAT + "." + getServer()) || Permissions.hasPerm(player, Permissions.General.CHAT)) && (senderP == null || !BungeeEssentials.getInstance().contains("ignore") || !pD.isIgnored(player.getUniqueId().toString(), senderP.getUniqueId().toString()))).forEach(player -> player.sendMessage(msg));
                if (msg != null) {
                    ProxyServer.getInstance().getConsole().sendMessage(msg);
                    Log.log("[GCHAT] " + msg.toLegacyText());
                }
            }
        }
    }

    public String toString() {
        return "ChatEvent(cancelled=" + this.isCancelled() + ", server=" + this.getServer() + ", sender=" + this.getSender() + ", message=" + this.getMessage() + ")";
    }
}
