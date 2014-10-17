package me.hadrondev;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Created by Connor Harries on 17/10/2014.
 */
public class Chat {
    private static WeakHashMap<UUID, UUID> messages = new WeakHashMap<>();

    public static void sendMessage(ProxiedPlayer player, String name, String message) {
        ProxiedPlayer recipient = BungeeEssentials.me.getProxy().getPlayer(name);
        if(recipient != null) {
            String msg = String.format("&a(%s) &7[%s » %s] &f%s", player.getServer().getInfo().getName(), player.getName(), recipient.getName(), message);
            msg = ChatColor.translateAlternateColorCodes('&', msg);

            player.sendMessage(msg);
            recipient.sendMessage(msg);

            messages.put(recipient.getUniqueId(), player.getUniqueId());
        } else {
            player.sendMessage(
                new ComponentBuilder("Sorry, that player is offline.").color(ChatColor.RED)
                    .create());
        }
    }

    public static UUID reply(ProxiedPlayer player) {
        for(Map.Entry<UUID, UUID> uuid : messages.entrySet()) {
            if(uuid.getKey() == player.getUniqueId()) {
                return uuid.getValue();
            }
        }
        return null;
    }
}
