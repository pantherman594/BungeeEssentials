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
