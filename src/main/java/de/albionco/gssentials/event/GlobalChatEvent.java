package de.albionco.gssentials.event;

import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Messenger;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

/**
 * Created by David on 8/2/2015.
 *
 * @author David
 */
public class GlobalChatEvent extends Event {
    private String server;
    private String sender;
    private String msg;

    public GlobalChatEvent(String server, String sender, String msg) {
        this.server = server;
        this.sender = sender;
        this.msg = msg;

        ProxiedPlayer senderP = null;
        if (ProxyServer.getInstance().getPlayer(sender) != null) {
            senderP = ProxyServer.getInstance().getPlayer(sender);
        }
        if (!(senderP != null && Messenger.isMutedF(senderP))) {
            if (msg != null) {
                msg = Dictionary.format(Dictionary.FORMAT_GCHAT, "SERVER", server, "SENDER", sender, "MESSAGE", msg);
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    if ((player.hasPermission(Permissions.General.CHAT + "." + server) || player.hasPermission(Permissions.General.CHAT)) && (!BungeeEssentials.getInstance().ignore() || !Messenger.isIgnoring(player, ProxyServer.getInstance().getPlayer(sender))))
                        player.sendMessage(msg);
                }
                ProxyServer.getInstance().getConsole().sendMessage(msg);
            }
        }
    }

    public String getServer() {
        return server;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return msg;
    }
}
