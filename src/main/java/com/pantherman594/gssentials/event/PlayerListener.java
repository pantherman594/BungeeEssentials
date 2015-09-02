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

import com.google.common.collect.Maps;
import com.pantherman594.gssentials.BungeeEssentials;
import com.pantherman594.gssentials.utils.Dictionary;
import com.pantherman594.gssentials.utils.Messenger;
import com.pantherman594.gssentials.utils.Permissions;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

@SuppressWarnings("deprecation")
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
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String sender = player.getName();
        String cmd = event.getMessage();
        if (cmd.startsWith("/")) {
            if (!player.hasPermission(Permissions.Admin.SPY_EXEMPT) && BungeeEssentials.getInstance().shouldCommandSpy()) {
                for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                    if ((onlinePlayer.getUniqueId() != player.getUniqueId()) && (onlinePlayer.hasPermission(Permissions.Admin.SPY_COMMAND)) && Messenger.isCSpy(onlinePlayer)) {
                        onlinePlayer.sendMessage(Dictionary.format(Dictionary.CSPY_COMMAND, "SENDER", sender, "COMMAND", cmd));
                    }
                }
            }
            return;
        }
        if (Messenger.isMutedF(player, event.getMessage())) {
            event.setCancelled(true);
            return;
        }
        if (Messenger.isStaffChat(player) && !event.isCancelled() && !event.isCommand()) {
            String server = player.getServer().getInfo().getName();
            String msg = Messenger.filter(player, event.getMessage());
            ProxyServer.getInstance().getPluginManager().callEvent(new StaffChatEvent(server, sender, msg));
            event.setCancelled(true);
        }
        if (Messenger.isGlobalChat(player) && !event.isCancelled() && !event.isCommand()) {
            String server = player.getServer().getInfo().getName();
            String msg = Messenger.filter(player, event.getMessage());
            ProxyServer.getInstance().getPluginManager().callEvent(new GlobalChatEvent(server, sender, msg));
            event.setCancelled(true);
        }
        if (BungeeEssentials.getInstance().useChatSpamProtection() || BungeeEssentials.getInstance().useChatRules()) {
            if (event.isCommand() || event.isCancelled()) {
                return;
            }
            Connection connection = event.getSender();
            for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                if (onlinePlayer.getAddress() == connection.getAddress()) {
                    Messenger.chat(onlinePlayer, event);
                    return;
                }
            }
        }
        if (BungeeEssentials.getInstance().logAll()) {
            BungeeEssentials.getInstance().getLogger().log(Level.INFO, Dictionary.format(Dictionary.FORMAT_CHAT, "PLAYER", sender, "MESSAGE", cmd));
        }
    }

    @EventHandler(priority = -65)
    public void login(LoginEvent event) {
        if (BungeeEssentials.getInstance().shouldWatchMultilog()) {
            InetAddress address = event.getConnection().getAddress().getAddress();
            if (connections.get(address) == null) {
                connections.put(address, 1);
            } else {
                if (connections.get(address) + 1 > max) {
                    event.setCancelled(true);
                    event.setCancelReason(Dictionary.format(Dictionary.MULTILOG_KICK_MESSAGE));
                }
            }
        }
    }

    @EventHandler(priority = Byte.MAX_VALUE)
    public void postLogin(PostLoginEvent event) {
        if (BungeeEssentials.getInstance().shouldWatchMultilog()) {
            InetAddress address = event.getPlayer().getAddress().getAddress();
            int newCount = connections.get(address) + 1;
            connections.put(address, newCount);
        }
        List<String> players = BungeeEssentials.getInstance().getPlayerConfig().getStringList("players");
        if (!players.contains(event.getPlayer().getName())) {
            BungeeEssentials.getInstance().savePlayerConfig(event.getPlayer().getName());
        }
        if (BungeeEssentials.getInstance().shouldAnnounce() && !(Messenger.isHidden(event.getPlayer())) && !(Dictionary.FORMAT_JOIN.equals("")) && event.getPlayer().hasPermission(Permissions.General.JOINANNC)) {
            ProxyServer.getInstance().broadcast(Dictionary.format(Dictionary.FORMAT_JOIN, "PLAYER", event.getPlayer().getName()));
        }
        if (BungeeEssentials.getInstance().logAll()) {
            BungeeEssentials.getInstance().getLogger().log(Level.INFO, Dictionary.format(Dictionary.FORMAT_JOIN, "PLAYER", event.getPlayer().getName()));
        }
    }


    @EventHandler(priority = Byte.MAX_VALUE)
    public void logout(PlayerDisconnectEvent event) {
        if (BungeeEssentials.getInstance().shouldWatchMultilog()) {
            InetAddress address = event.getPlayer().getAddress().getAddress();
            Integer amount = connections.get(address);
            connections.remove(address);
            if (amount != null && amount > 1) {
                connections.put(address, amount - 1);
            }
        }
        if (BungeeEssentials.getInstance().shouldAnnounce() && !(Messenger.isHidden(event.getPlayer())) && !(Dictionary.FORMAT_QUIT.equals("")) && event.getPlayer().hasPermission(Permissions.General.QUITANNC)) {
            ProxyServer.getInstance().broadcast(Dictionary.format(Dictionary.FORMAT_QUIT, "PLAYER", event.getPlayer().getName()));
        }
        if (BungeeEssentials.getInstance().logAll()) {
            BungeeEssentials.getInstance().getLogger().log(Level.INFO, Dictionary.format(Dictionary.FORMAT_QUIT, "PLAYER", event.getPlayer().getName()));
        }
    }

    @EventHandler(priority = Byte.MAX_VALUE)
    public void kick(ServerKickEvent event) {
        if (BungeeEssentials.getInstance().logAll()) {
            BungeeEssentials.getInstance().getLogger().log(Level.INFO, Dictionary.format(Dictionary.FORMAT_KICK, "PLAYER", event.getPlayer().getName(), "REASON", event.getKickReason()));
        }
    }

    @EventHandler(priority = Byte.MAX_VALUE)
    public void ping(ProxyPingEvent event) {
        ServerPing response = event.getResponse();
        ServerPing.Players players = response.getPlayers();
        players = new ServerPing.Players(players.getMax(), players.getOnline() - Messenger.hiddenNum(), players.getSample());
        final ServerPing ping = new ServerPing(response.getVersion(), players, response.getDescription(), response.getFaviconObject());
        event.setResponse(ping);
    }

    @EventHandler(priority = Byte.MAX_VALUE)
    public void tab(TabCompleteResponseEvent event) {
        List<String> suggestions = event.getSuggestions();
        List<String> remove = new ArrayList<>();
        for (String suggestion : suggestions) {
            if (ProxyServer.getInstance().getPlayer(suggestion) instanceof ProxiedPlayer && Messenger.isHidden(ProxyServer.getInstance().getPlayer(suggestion))) {
                remove.add(suggestion);
            }
        }
        for (String player : remove) {
            suggestions.remove(player);
        }
    }
}
