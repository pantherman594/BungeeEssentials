package de.albionco.gssentials.event;

import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

/**
 * Created by David on 8/2/2015.
 *
 * @author David
 */
public class StaffChatEvent extends Event {
    private String server;
    private String sender;
    private String msg;

    public StaffChatEvent(String server, String sender, String msg) {
        this.server = server;
        this.sender = sender;
        this.msg = msg;

        if (msg != null) {
            msg = Dictionary.format(Dictionary.FORMAT_STAFF_CHAT, "SERVER", server, "SENDER", sender, "MESSAGE", msg);
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (player.hasPermission(Permissions.Admin.CHAT + "." + server) || player.hasPermission(Permissions.Admin.CHAT)) {
                    player.sendMessage(msg);
                }
            }
            ProxyServer.getInstance().getConsole().sendMessage(msg);
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
