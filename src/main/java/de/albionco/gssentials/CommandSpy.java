/*
 * Copyright (c) 2015 David Shen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.albionco.gssentials;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by David on 4/19/2015.
 */
@SuppressWarnings("deprecation")
public class CommandSpy implements Listener {

    @EventHandler
    public void onMsg(ChatEvent msg) {
        ProxiedPlayer player = (ProxiedPlayer) msg.getSender();
        String sender = player.getName();
        String cmd = msg.getMessage();
        if ((cmd.startsWith("/")) && (!player.hasPermission(Permissions.Admin.SPY_EXEMPT))) {
            for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                if ((onlinePlayer.getUniqueId() != player.getUniqueId()) && (onlinePlayer.hasPermission(Permissions.Admin.SPY)) && Messenger.isSpy(onlinePlayer)) {
                    onlinePlayer.sendMessage(Dictionary.format(Dictionary.SPY_COMMAND, "SENDER", sender, "COMMAND", cmd));
                }
            }
        }
    }
}
