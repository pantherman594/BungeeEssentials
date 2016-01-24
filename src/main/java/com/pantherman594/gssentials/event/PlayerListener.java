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
import com.pantherman594.gssentials.utils.*;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PlayerListener implements Listener {
    private final HashSet<InetAddress> connections;
    private final Map<InetAddress, ServerInfo> redirServer;
    public Map<UUID, String> cmds = new HashMap<>();

    public PlayerListener() {
        connections = new HashSet<>();
        redirServer = new HashMap<>();
    }

    @EventHandler(priority = Byte.MAX_VALUE)
    public void chat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        PlayerData pD = PlayerData.getData(player.getUniqueId());
        String sender = player.getName();
        if (event.isCommand()) {
            String cmd = event.getMessage().substring(1);
            if (BungeeEssentials.getInstance().contains("spam-command") && !player.hasPermission(Permissions.Admin.BYPASS_FILTER)) {
                if (cmds.get(player.getUniqueId()) != null && Messenger.compare(cmd, cmds.get(player.getUniqueId())) > 0.85) {
                    player.sendMessage(Dictionary.format(Dictionary.WARNING_LEVENSHTEIN_DISTANCE));
                    event.setCancelled(true);
                }
                cmds.put(player.getUniqueId(), cmd);
            }
            if (!event.isCancelled() && !player.hasPermission(Permissions.Admin.SPY_EXEMPT) && BungeeEssentials.getInstance().contains("commandSpy")) {
                for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                    if ((onlinePlayer.getUniqueId() != player.getUniqueId()) && (onlinePlayer.hasPermission(Permissions.Admin.SPY_COMMAND)) && PlayerData.getData(onlinePlayer.getUniqueId()).isCSpy()) {
                        onlinePlayer.sendMessage(Dictionary.format(Dictionary.CSPY_COMMAND, "SENDER", sender, "COMMAND", event.getMessage()));
                    }
                }
            }
            return;
        } else {
            if (Messenger.isMutedF(player, event.getMessage())) {
                event.setCancelled(true);
                return;
            }
            if (pD.isStaffChat() && !event.isCancelled() && !event.isCommand()) {
                String server = player.getServer().getInfo().getName();
                String msg = Messenger.filter(player, event.getMessage(), Messenger.ChatType.PUBLIC);
                ProxyServer.getInstance().getPluginManager().callEvent(new StaffChatEvent(server, sender, msg));
                event.setCancelled(true);
            }
            if (pD.isGlobalChat() && !event.isCancelled() && !event.isCommand()) {
                String server = player.getServer().getInfo().getName();
                String msg = Messenger.filter(player, event.getMessage(), Messenger.ChatType.PUBLIC);
                ProxyServer.getInstance().getPluginManager().callEvent(new GlobalChatEvent(server, sender, msg));
                event.setCancelled(true);
            }
            if (BungeeEssentials.getInstance().contains("spam-chat", "rules-chat")) {
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
        }
        if (BungeeEssentials.getInstance().contains("fulllog")) {
            Log.log(Dictionary.format(Dictionary.FORMAT_CHAT, "PLAYER", sender, "MESSAGE", event.getMessage()).toLegacyText());
        }
    }

    @EventHandler(priority = -65)
    public void login(final LoginEvent event) {
        if (BungeeEssentials.getInstance().contains("fastRelog")) {
            if (connections.contains(event.getConnection().getAddress().getAddress())) {
                event.setCancelled(true);
                event.setCancelReason(Dictionary.format(Dictionary.FAST_RELOG_KICK).toLegacyText());
                return;
            }
            connections.add(event.getConnection().getAddress().getAddress());
            ProxyServer.getInstance().getScheduler().schedule(BungeeEssentials.getInstance(), new Runnable() {
                @Override
                public void run() {
                    connections.remove(event.getConnection().getAddress().getAddress());
                }
            }, 5, TimeUnit.SECONDS);
        }
        if (BungeeEssentials.getInstance().contains("autoredirect")) {
            String[] ip = event.getConnection().getVirtualHost().getHostName().split("\\.");
            for (ServerInfo info : ProxyServer.getInstance().getServers().values()) {
                if (info.getName().equalsIgnoreCase(ip[0])) {
                    redirServer.put(event.getConnection().getAddress().getAddress(), info);
                    break;
                }
            }
        }
    }

    @EventHandler(priority = Byte.MAX_VALUE)
    public void postLogin(PostLoginEvent event) {
        new PlayerData(event.getPlayer().getUniqueId().toString(), event.getPlayer().getName());
        List<String> players = BungeeEssentials.getInstance().getPlayerConfig().getStringList("players");
        if (!players.contains(event.getPlayer().getName())) {
            BungeeEssentials.getInstance().savePlayerConfig(event.getPlayer().getName());
        }
        if (BungeeEssentials.getInstance().contains("joinAnnounce") && !PlayerData.getData(event.getPlayer().getUniqueId()).isHidden() && !(Dictionary.FORMAT_JOIN.equals("")) && event.getPlayer().hasPermission(Permissions.General.JOINANNC) && !(BungeeEssentials.getInstance().isIntegrated() && BungeeEssentials.getInstance().getIntegrationProvider().isBanned(event.getPlayer()))) {
            ProxyServer.getInstance().broadcast(Dictionary.format(Dictionary.FORMAT_JOIN, "PLAYER", event.getPlayer().getName()));
        }
        if (BungeeEssentials.getInstance().contains("fulllog")) {
            Log.log(Dictionary.format(Dictionary.FORMAT_JOIN, "PLAYER", event.getPlayer().getName()).toLegacyText());
        }
    }

    @EventHandler(priority = Byte.MAX_VALUE)
    public void connect(ServerConnectedEvent event) {
        if (redirServer.containsKey(event.getPlayer().getAddress().getAddress())) {
            ServerInfo info = redirServer.get(event.getPlayer().getAddress().getAddress());
            if (info.canAccess(event.getPlayer())) {
                event.getPlayer().connect(info);
            }
            redirServer.remove(event.getPlayer().getAddress().getAddress());
        }
    }

    @EventHandler(priority = Byte.MAX_VALUE)
    public void logout(final PlayerDisconnectEvent event) {
        if (BungeeEssentials.getInstance().contains("fastRelog")) {
            if (!connections.contains(event.getPlayer().getAddress().getAddress())) {
                connections.add(event.getPlayer().getAddress().getAddress());
                ProxyServer.getInstance().getScheduler().schedule(BungeeEssentials.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        connections.remove(event.getPlayer().getAddress().getAddress());
                    }
                }, 3, TimeUnit.SECONDS);
            }
        }
        if (BungeeEssentials.getInstance().contains("joinAnnounce") && !PlayerData.getData(event.getPlayer().getUniqueId()).isHidden() && !(Dictionary.FORMAT_QUIT.equals("")) && event.getPlayer().hasPermission(Permissions.General.QUITANNC) && !(BungeeEssentials.getInstance().isIntegrated() && BungeeEssentials.getInstance().getIntegrationProvider().isBanned(event.getPlayer()))) {
            ProxyServer.getInstance().broadcast(Dictionary.format(Dictionary.FORMAT_QUIT, "PLAYER", event.getPlayer().getName()));
        }
        if (BungeeEssentials.getInstance().contains("fulllog")) {
            Log.log(Dictionary.format(Dictionary.FORMAT_QUIT, "PLAYER", event.getPlayer().getName()).toLegacyText());
        }
        PlayerData.getData(event.getPlayer().getUniqueId()).save();
    }

    @EventHandler(priority = Byte.MAX_VALUE)
    public void kick(ServerKickEvent event) {
        if (BungeeEssentials.getInstance().contains("fulllog")) {
            Log.log(Dictionary.format(Dictionary.FORMAT_KICK, "PLAYER", event.getPlayer().getName(), "REASON", event.getKickReason()).toLegacyText());
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
            if (ProxyServer.getInstance().getPlayer(suggestion) instanceof ProxiedPlayer && PlayerData.getData(ProxyServer.getInstance().getPlayer(suggestion).getUniqueId()).isHidden()) {
                remove.add(suggestion);
            }
        }
        for (String player : remove) {
            suggestions.remove(player);
        }
    }
}
