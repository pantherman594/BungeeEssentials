package de.albionco.gssentials.event;

import com.google.common.collect.Maps;
import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Messenger;
import de.albionco.gssentials.utils.Permissions;
import de.albionco.gssentials.utils.Updater;
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
    public void login(PreLoginEvent event) {
        if (BungeeEssentials.getInstance().shouldWatchMultilog()) {
            InetAddress address = event.getConnection().getAddress().getAddress();
            if (connections.get(address) == null) {
                connections.put(address, 1);
            } else {
                int currPlayers = 0;
                for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                    if (p.getAddress().getAddress().toString().equals(address.toString())) {
                        currPlayers++;
                    }
                }
                if (currPlayers != connections.get(address)) {
                    connections.put(address, currPlayers);
                }
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
    public void postLogin(PostLoginEvent event) {
        List<String> players = BungeeEssentials.getInstance().getPlayerConfig().getStringList("players");
        if (!players.contains(event.getPlayer().getName())) {
            BungeeEssentials.getInstance().savePlayerConfig(event.getPlayer().getName());
        }
        if (BungeeEssentials.getInstance().shouldAnnounce() && !(Messenger.isHidden(event.getPlayer())) && !(Dictionary.FORMAT_JOIN.equals("")) && event.getPlayer().hasPermission(Permissions.General.JOINANNC)) {
            ProxyServer.getInstance().broadcast(Dictionary.format(Dictionary.FORMAT_JOIN, "PLAYER", event.getPlayer().getName()));
        }
        if (Updater.hasConfigChange() && event.getPlayer().hasPermission(Permissions.Admin.UPDATE)) {
            event.getPlayer().sendMessage(Dictionary.format("&cBungeeEssentials updated with a config change."));
            event.getPlayer().sendMessage(Dictionary.format("&cGo to http://www.spigotmc.org/resources/bungeeessentials.1488/ to compare and update your config."));
        }
        if (BungeeEssentials.getInstance().logAll()) {
            BungeeEssentials.getInstance().getLogger().log(Level.INFO, Dictionary.format(Dictionary.FORMAT_JOIN, "PLAYER", event.getPlayer().getName()));
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
