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

package de.albionco.gssentials;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Connor Harries on 17/10/2014.
 *
 * @author Connor Spencer Harries
 */
@SuppressWarnings("deprecation")
public class Messenger implements Listener {
    private static HashMap<UUID, UUID> messages = new HashMap<>();
    private static Set<UUID> spies = new HashSet<>();

    public static void sendMessage(CommandSender sender, ProxiedPlayer recipient, String message) {
        if (recipient != null) {
            ProxiedPlayer player = null;
            if (sender instanceof ProxiedPlayer) {
                player = (ProxiedPlayer) sender;
            }

            String server = player != null ? player.getServer().getInfo().getName() : "CONSOLE";

            String msg = Dictionary.format(Dictionary.FORMAT_MESSAGE, "SERVER", server, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message);

            sender.sendMessage(msg);
            recipient.sendMessage(msg);

            if (player != null) {
                if (!player.hasPermission(Permissions.Admin.SPY_EXEMPT)) {
                    String spyMessage = Dictionary.format(Dictionary.FORMAT_SPY_MESSAGE, "SERVER", server, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message);
                    for (ProxiedPlayer onlinePlayer : player.getServer().getInfo().getPlayers()) {
                        if (onlinePlayer.getUniqueId() != player.getUniqueId() && onlinePlayer.getUniqueId() != recipient.getUniqueId()) {
                            if (onlinePlayer.hasPermission(Permissions.Admin.SPY)) {
                                onlinePlayer.sendMessage(spyMessage);
                            }
                        }
                    }
                }
                messages.put(recipient.getUniqueId(), player.getUniqueId());
            }
        } else {
            sender.sendMessage(Dictionary.colour(Dictionary.ERRORS_OFFLINE));
        }
    }

    public static UUID reply(ProxiedPlayer player) {
        return messages.get(player.getUniqueId());
    }

    public static boolean isSpy(ProxiedPlayer player) {
        Preconditions.checkNotNull(player);
        return isSpy(player.getUniqueId());
    }

    public static boolean isSpy(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        return spies.contains(uuid);
    }

    public static boolean addSpy(ProxiedPlayer player) {
        Preconditions.checkNotNull(player);
        return addSpy(player.getUniqueId());
    }

    public static boolean addSpy(UUID uuid) {
        Preconditions.checkArgument(!spies.contains(uuid), "Player is already a spy!");
        return spies.add(uuid);
    }

    public static boolean removeSpy(ProxiedPlayer player) {
        Preconditions.checkNotNull(player);
        return removeSpy(player.getUniqueId());
    }

    public static boolean removeSpy(UUID uuid) {
        Preconditions.checkArgument(spies.contains(uuid), "Player is not a spy!");
        return spies.remove(uuid);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void logout(PlayerDisconnectEvent event) {
        if (messages.containsKey(event.getPlayer().getUniqueId())) {
            messages.remove(event.getPlayer().getUniqueId());
        }
    }
}
