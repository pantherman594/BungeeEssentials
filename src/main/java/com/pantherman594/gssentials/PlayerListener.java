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

package com.pantherman594.gssentials;

import com.pantherman594.gssentials.database.PlayerData;
import com.pantherman594.gssentials.event.GlobalChatEvent;
import com.pantherman594.gssentials.event.StaffChatEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class PlayerListener implements Listener {
    private final HashSet<InetAddress> connections;
    private final Map<InetAddress, ServerInfo> redirServer;
    private Map<UUID, String> cmds;
    private Map<UUID, Long> cmdLog;

    private Messenger mess;
    private PlayerData pD;

    PlayerListener() {
        connections = new HashSet<>();
        redirServer = new HashMap<>();
        cmds = new HashMap<>();
        cmdLog = new HashMap<>();
        mess = BungeeEssentials.getInstance().getMessenger();
        pD = BungeeEssentials.getInstance().getPlayerData();
    }

    /**
     * Event fired when a player chats. The message is filtered, formatted,
     * and sent to the correct channel. May be logged if fulllog is enabled.
     *
     * @param event The Chat Event.
     */
    @EventHandler(priority = Byte.MAX_VALUE)
    public void chat(ChatEvent event) {
        final ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String uuid = player.getUniqueId().toString();
        String sender = player.getName();
        if (event.isCommand()) {
            if (BungeeEssentials.getInstance().contains("fulllog")) {
                Log.log(Dictionary.format("[COMMAND] " + Dictionary.FORMAT_CHAT, "PLAYER", sender, "MESSAGE", event.getMessage()).toLegacyText());
            }
            String cmd = event.getMessage().substring(1);
            if (BungeeEssentials.getInstance().contains("spam-command") && !Permissions.hasPerm(player, Permissions.Admin.BYPASS_FILTER)) {
                if (cmds.get(player.getUniqueId()) != null && cmd.equals(cmds.get(player.getUniqueId())) && cmdLog.containsKey(player.getUniqueId())) {
                    player.sendMessage(Dictionary.format(Dictionary.WARNING_LEVENSHTEIN_DISTANCE));
                    event.setCancelled(true);
                }
                cmds.put(player.getUniqueId(), cmd);
                final long time = System.currentTimeMillis();
                cmdLog.put(player.getUniqueId(), time);
                ProxyServer.getInstance().getScheduler().schedule(BungeeEssentials.getInstance(), () -> {
                    if (cmdLog.containsKey(player.getUniqueId()) && cmdLog.get(player.getUniqueId()).equals(time)) {
                        cmdLog.remove(player.getUniqueId());
                    }
                }, 5, TimeUnit.SECONDS);
            }
            if (!event.isCancelled() && !Permissions.hasPerm(player, Permissions.Admin.SPY_EXEMPT) && BungeeEssentials.getInstance().contains("commandSpy")) {
                ProxyServer.getInstance().getPlayers().stream().filter(onlinePlayer -> (onlinePlayer.getUniqueId() != player.getUniqueId()) && (Permissions.hasPerm(onlinePlayer, Permissions.Admin.SPY_COMMAND)) && pD.isCSpy(onlinePlayer.getUniqueId().toString())).forEach(onlinePlayer -> onlinePlayer.sendMessage(Dictionary.format(Dictionary.CSPY_COMMAND, "SENDER", sender, "COMMAND", event.getMessage())));
                if (pD.isCSpy("CONSOLE")) {
                    ProxyServer.getInstance().getConsole().sendMessage(Dictionary.format(Dictionary.CSPY_COMMAND, "SENDER", sender, "COMMAND", event.getMessage()).toLegacyText());
                }
            }
            if (BungeeEssentials.getInstance().contains("server") && Permissions.hasPerm(player, Permissions.General.LIST) && cmd.split(" ")[0].startsWith("server") && cmd.split(" ").length == 1) {
                event.setCancelled(true);
                ProxyServer.getInstance().getPluginManager().dispatchCommand(player, BungeeEssentials.getInstance().getMain("list"));
            }
        } else {

            if (BungeeEssentials.getInstance().contains("fulllog")) {
                Log.log(Dictionary.format("[CHAT] " + Dictionary.FORMAT_CHAT, "PLAYER", sender, "MESSAGE", event.getMessage()).toLegacyText());
            }
            if (mess.isMutedF(player, event.getMessage())) {
                event.setCancelled(true);
                return;
            }

            if (!event.isCancelled() && !event.isCommand()) {
                if (Permissions.hasPerm(player, Permissions.Admin.CHAT) && pD.isStaffChat(uuid)) {
                    String server = player.getServer().getInfo().getName();
                    ProxyServer.getInstance().getPluginManager().callEvent(new StaffChatEvent(server, sender, event.getMessage()));
                    event.setCancelled(true);
                } else if (Permissions.hasPerm(player, Permissions.General.CHAT) && pD.isGlobalChat(uuid)) {
                    String server = player.getServer().getInfo().getName();
                    ProxyServer.getInstance().getPluginManager().callEvent(new GlobalChatEvent(server, sender, event.getMessage()));
                    event.setCancelled(true);
                }
            }
            if (BungeeEssentials.getInstance().contains("spam-chat", "rules-chat")) {
                if (event.isCommand() || event.isCancelled()) {
                    return;
                }
                mess.chat(player, event);
            }
        }
    }

    /**
     * Event fired when a player first connects. Checked for fast relog and may be
     * redirected to correct server, depending on ip.
     *
     * @param event The Login Event.
     */
    @EventHandler(priority = -65)
    public void login(final LoginEvent event) {
        if (BungeeEssentials.getInstance().contains("fastRelog")) {
            if (connections.contains(event.getConnection().getAddress().getAddress())) {
                event.setCancelled(true);
                event.setCancelReason(Dictionary.format(Dictionary.FAST_RELOG_KICK).toLegacyText());
                return;
            }
            connections.add(event.getConnection().getAddress().getAddress());
            ProxyServer.getInstance().getScheduler().schedule(BungeeEssentials.getInstance(), () -> connections.remove(event.getConnection().getAddress().getAddress()), 5, TimeUnit.SECONDS);
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

    /**
     * Event fired when a player completely logs in. A new PlayerData is created and
     * saved, and the player is added to the playerlist and saved. Login is announced
     * and logged.
     *
     * @param event The Post Login Event.
     */
    @EventHandler(priority = Byte.MAX_VALUE)
    public void postLogin(PostLoginEvent event) {
        pD.setName(event.getPlayer().getUniqueId().toString(), event.getPlayer().getName());
        pD.setIp(event.getPlayer().getUniqueId().toString(), event.getPlayer().getAddress().getAddress().getHostAddress());
        if (BungeeEssentials.getInstance().contains("joinAnnounce") && !pD.isHidden(event.getPlayer().getUniqueId().toString()) && !(Dictionary.FORMAT_JOIN.equals("")) && Permissions.hasPerm(event.getPlayer(), Permissions.General.JOINANNC) && !(BungeeEssentials.getInstance().isIntegrated() && BungeeEssentials.getInstance().getIntegrationProvider().isBanned(event.getPlayer()))) {
            ProxyServer.getInstance().broadcast(Dictionary.format(Dictionary.FORMAT_JOIN, "PLAYER", event.getPlayer().getName()));
        }
        if (BungeeEssentials.getInstance().contains("fulllog")) {
            Log.log(Dictionary.format("[JOIN] " + Dictionary.FORMAT_JOIN, "PLAYER", event.getPlayer().getName()).toLegacyText());
        }
    }

    /**
     * Event fired when a player connects to a server. If player is set to be redirected,
     * player is sent to that server and redirection is removed.
     *
     * @param event The Server Connected Event.
     */
    @EventHandler(priority = Byte.MAX_VALUE)
    public void connect(ServerConnectedEvent event) {
        if (redirServer.containsKey(event.getPlayer().getAddress().getAddress())) {
            ServerInfo info = redirServer.get(event.getPlayer().getAddress().getAddress());
            if (info.canAccess(event.getPlayer())) {
                event.getPlayer().connect(info);
            }
            redirServer.remove(event.getPlayer().getAddress().getAddress());
        }
        if (BungeeEssentials.getInstance().contains("fulllog")) {
            Log.log(Dictionary.format("[CONNECT] {{ PLAYER }} connected to {{ SERVER }}.", "PLAYER", event.getPlayer().getName(), "SERVER", event.getServer().getInfo().getName()).toLegacyText());
        }
    }

    /**
     * Event fired when a player disconnects from the server. Player is saved for
     * fast relog, logout is announced and logged, and the PlayerData is saved and
     * removed from the registered list.
     *
     * @param event The Disconnect Event.
     */
    @EventHandler(priority = Byte.MAX_VALUE)
    public void logout(final PlayerDisconnectEvent event) {
        if (BungeeEssentials.getInstance().contains("fastRelog")) {
            if (!connections.contains(event.getPlayer().getAddress().getAddress())) {
                connections.add(event.getPlayer().getAddress().getAddress());
                ProxyServer.getInstance().getScheduler().schedule(BungeeEssentials.getInstance(), () -> connections.remove(event.getPlayer().getAddress().getAddress()), 3, TimeUnit.SECONDS);
            }
        }
        if (BungeeEssentials.getInstance().contains("joinAnnounce") && !(Dictionary.FORMAT_QUIT.equals("")) && Permissions.hasPerm(event.getPlayer(), Permissions.General.QUITANNC) && !pD.isHidden(event.getPlayer().getUniqueId().toString())) {
            ProxyServer.getInstance().broadcast(Dictionary.format(Dictionary.FORMAT_QUIT, "PLAYER", event.getPlayer().getName()));
        }
        if (BungeeEssentials.getInstance().contains("fulllog")) {
            Log.log(Dictionary.format("[QUIT] " + Dictionary.FORMAT_QUIT, "PLAYER", event.getPlayer().getName()).toLegacyText());
        }
    }

    /**
     * Event fired when a player is kicked from the proxy. Event is logged. If the player
     * is kicked because of a server shutdown, the player is redirected to the default
     * servers.
     *
     * @param event The Server Kick Event.
     */
    @EventHandler(priority = Byte.MAX_VALUE)
    public void kick(ServerKickEvent event) {
        if (BungeeEssentials.getInstance().contains("fulllog")) {
            Log.log(Dictionary.format("[KICK] " + Dictionary.FORMAT_KICK, "PLAYER", event.getPlayer().getName(), "REASON", event.getKickReason()).toLegacyText());
        }
        if (event.getKickReasonComponent()[0].toPlainText().equals("Server closed")) {
            sendFallback(event);
        } else {
            try (Socket s = new Socket()) {
                s.connect(event.getKickedFrom().getAddress());
            } catch (IOException e) {
                sendFallback(event);
            }
        }
    }

    /**
     * Event fired when a player pings the proxy. If there are hidden players, they are
     * removed from the player count and the player list.
     *
     * @param event The Proxy Ping Event.
     */
    @EventHandler(priority = Byte.MAX_VALUE)
    public void ping(ProxyPingEvent event) {
        ServerPing response = event.getResponse();
        ServerPing.Players players = response.getPlayers();

        if (BungeeEssentials.getInstance().contains("hoverlist") && !ProxyServer.getInstance().getPlayers().isEmpty()) {

            String uuid = (String) pD.getData("ip", event.getConnection().getAddress().getAddress().getHostAddress(), "uuid");

            UUID EMPTY_UUID = UUID.fromString("0-0-0-0-0");
            List<ServerPing.PlayerInfo> infos = new ArrayList<>();
            List<ServerPing.PlayerInfo> friends = new ArrayList<>();

            if (uuid != null) {
                for (String fUuid : pD.getFriends(uuid)) {
                    ProxiedPlayer fP;
                    if ((fP = ProxyServer.getInstance().getPlayer(UUID.fromString(fUuid))) != null) {
                        if (!pD.isHidden(fUuid)) {
                            friends.add(new ServerPing.PlayerInfo(fP.getName(), fUuid));
                        }
                    }
                }

                if (!friends.isEmpty()) {
                    friends.add(0, new ServerPing.PlayerInfo(Dictionary.color(Dictionary.HOVER_FRIEND_HEADER), EMPTY_UUID));
                }
            }

            List<ServerPing.PlayerInfo> staff = new ArrayList<>();
            List<ServerPing.PlayerInfo> other = new ArrayList<>();

            for (ProxiedPlayer p : mess.getVisiblePlayers(false)) {
                ServerPing.PlayerInfo info = new ServerPing.PlayerInfo(p.getName(), p.getUniqueId());
                if (!friends.contains(info)) {
                    if (Permissions.hasPerm(p, Permissions.Admin.HOVER_LIST)) {
                        staff.add(info);
                    } else {
                        other.add(info);
                    }
                }
            }

            if (!staff.isEmpty()) {
                staff.add(0, new ServerPing.PlayerInfo(Dictionary.color(Dictionary.HOVER_STAFF_HEADER), EMPTY_UUID));
            }

            if (!other.isEmpty()) {
                other.add(0, new ServerPing.PlayerInfo(Dictionary.color(Dictionary.HOVER_OTHER_HEADER), EMPTY_UUID));
            }

            Map<String, List<ServerPing.PlayerInfo>> orders = new TreeMap<>();
            orders.put(Dictionary.HOVER_FRIEND_ORDER, friends);
            orders.put(Dictionary.HOVER_STAFF_ORDER, staff);
            orders.put(Dictionary.HOVER_OTHER_ORDER, other);

            orders.keySet().stream().filter(order -> Integer.valueOf(order) > 0).forEach(order -> infos.addAll(orders.get(order)));

            players.setSample(infos.toArray(new ServerPing.PlayerInfo[infos.size() > 12 ? 12 : infos.size()]));
            players.setOnline(mess.getVisiblePlayers(false).size());
            response.setPlayers(players);
            event.setResponse(response);
        }
    }

    /**
     * Event fired when a player tries tab-completion. Hidden players
     * are removed from the suggestions.
     *
     * @param event The Tab Complete Response Event.
     */
    @EventHandler(priority = Byte.MAX_VALUE)
    public void tab(TabCompleteResponseEvent event) {
        List<String> suggestions = event.getSuggestions();
        List<String> remove = suggestions.stream().filter(suggestion -> ProxyServer.getInstance().getPlayer(suggestion) instanceof ProxiedPlayer && pD.isHidden(ProxyServer.getInstance().getPlayer(suggestion).getUniqueId().toString())).collect(Collectors.toList());
        remove.forEach(suggestions::remove);
    }

    /**
     * If a player is kicked due to a server shutdown, the player is sent to
     * the default servers if they are online.
     *
     * @param e The Server Kick Event.
     */
    private void sendFallback(ServerKickEvent e) {
        e.setCancelled(true);
        for (String server : e.getPlayer().getPendingConnection().getListener().getServerPriority()) {
            ServerInfo info = ProxyServer.getInstance().getServerInfo(server);
            try (Socket s = new Socket()) {
                s.connect(info.getAddress());
                e.getPlayer().connect(info);
                break;
            } catch (IOException ignored) {
            }
        }
    }
}
