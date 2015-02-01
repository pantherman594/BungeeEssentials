/*
 * Copyright (c) 2015 Connor Spencer Harries
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

package de.albionco.gssentials.event;

import com.google.common.collect.Maps;
import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.Dictionary;
import de.albionco.gssentials.Messenger;
import de.albionco.gssentials.Permissions;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetAddress;
import java.util.HashMap;

/**
 * Created by Connor Harries on 31/01/2015.
 *
 * @author Connor Spencer Harries
 */
@SuppressWarnings("unused")
public class PlayerListener implements Listener {
    private final HashMap<InetAddress, Integer> connections;
    private final int max;

    public PlayerListener() {
        connections = Maps.newHashMap();
        int max = BungeeEssentials.getInstance().getConfig().getInt("multilog.limit", 3);
        if (max < 1) {
            max = 1;
        }
        this.max = max;
    }

    @EventHandler(priority = Byte.MAX_VALUE)
    public void chat(ChatEvent event) {
        if (BungeeEssentials.getInstance().useChatSpamProtetion() || BungeeEssentials.getInstance().useChatRules()) {
            if (event.isCommand() || event.isCancelled()) {
                return;
            }
            Connection connection = event.getSender();
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (player.getAddress() == connection.getAddress() && !player.hasPermission(Permissions.Admin.BYPASS_FILTER)) {
                    Messenger.chat(player, event);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = Byte.MAX_VALUE)
    public void login(PreLoginEvent event) {
        if (BungeeEssentials.getInstance().shouldWatchMultilog()) {
            InetAddress address = event.getConnection().getAddress().getAddress();
            if (connections.get(address) == null) {
                connections.put(address, 1);
            } else {
                int newCount = connections.get(address) + 1;
                if (newCount > max) {
                    event.setCancelled(true);
                    event.setCancelReason(Dictionary.format(Dictionary.MULTILOG_KICK_MESSAGE));
                    return;
                }
                connections.put(address, newCount);
            }
        }
    }

    @EventHandler(priority = Byte.MAX_VALUE)
    public void logout(PlayerDisconnectEvent event) {
        if (BungeeEssentials.getInstance().shouldWatchMultilog()) {
            InetAddress address = event.getPlayer().getAddress().getAddress();
            Integer amount = connections.remove(address);
            if (amount != null && amount > 1) {
                connections.put(address, amount - 1);
            }
        }
    }
}
