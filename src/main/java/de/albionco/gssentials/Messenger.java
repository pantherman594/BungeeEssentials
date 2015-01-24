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
import de.albionco.gssentials.regex.RuleManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;

/**
 * Created by Connor Harries on 17/10/2014.
 *
 * @author Connor Spencer Harries
 */
@SuppressWarnings("deprecation")
public class Messenger implements Listener {
    private static HashMap<UUID, UUID> messages = new HashMap<>();
    private static Set<UUID> hidden = new HashSet<>();
    private static Set<UUID> spies = new HashSet<>();

    public static void sendMessage(CommandSender sender, ProxiedPlayer recipient, String message) {
        if (recipient != null && !Messenger.isHidden(recipient)) {
            ProxiedPlayer player = null;
            if (sender instanceof ProxiedPlayer) {
                player = (ProxiedPlayer) sender;
                if (BungeeEssentials.getInstance().useRules()) {
                    RuleManager.MatchResult result = RuleManager.matches(message);
                    if (result.matched()) {
                        switch (result.getRule().getHandle()) {
                            case ADVERTISEMENT:
                                sender.sendMessage(Dictionary.format(Dictionary.WARNINGS_ADVERTISING));
                                return;
                            case CURSING:
                                sender.sendMessage(Dictionary.format(Dictionary.WARNINGS_SWEARING));
                                return;
                            case REPLACE:
                                if (result.getRule().getReplacement() != null) {
                                    Matcher matcher = result.getRule().getPattern().matcher(message);
                                    if (matcher.matches()) {
                                        message = matcher.replaceAll(result.getRule().getReplacement());
                                    }
                                }
                                break;
                        }
                    }
                }
            }

            String server = player != null ? player.getServer().getInfo().getName() : "CONSOLE";
            sender.sendMessage(Dictionary.format(Dictionary.FORMAT_MESSAGE, "SERVER", recipient.getServer().getInfo().getName(), "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));
            recipient.sendMessage(Dictionary.format(Dictionary.FORMAT_MESSAGE, "SERVER", server, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));

            if (player != null) {
                if (!sender.hasPermission(Permissions.Admin.SPY_EXEMPT)) {
                    String spyMessage = Dictionary.format(Dictionary.SPY_MESSAGE, "SERVER", server, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message);
                    for (ProxiedPlayer onlinePlayer : player.getServer().getInfo().getPlayers()) {
                        if (onlinePlayer.getUniqueId() != player.getUniqueId() && onlinePlayer.getUniqueId() != recipient.getUniqueId()) {
                            if (onlinePlayer.hasPermission(Permissions.Admin.SPY) && Messenger.isSpy(onlinePlayer)) {
                                onlinePlayer.sendMessage(spyMessage);
                            }
                        }
                    }
                }
                messages.put(recipient.getUniqueId(), player.getUniqueId());
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERRORS_OFFLINE));
        }
    }

    public static UUID reply(ProxiedPlayer player) {
        return messages.get(player.getUniqueId());
    }

    public static boolean isSpy(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        return spies.contains(player.getUniqueId());
    }

    public static boolean isHidden(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        return hidden.contains(player.getUniqueId());
    }

    public static boolean toggleSpy(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        if (isSpy(player)) {
            spies.remove(player.getUniqueId());
        } else {
            spies.add(player.getUniqueId());
        }
        return isSpy(player);
    }

    public static boolean toggleHidden(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        if (isHidden(player)) {
            hidden.remove(player.getUniqueId());
        } else {
            hidden.add(player.getUniqueId());
        }
        return isHidden(player);
    }

    public static void reset() {
        messages.clear();
        hidden.clear();
        spies.clear();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void logout(PlayerDisconnectEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (messages.containsKey(uuid)) {
            messages.remove(uuid);
        }

        if (spies.contains(uuid)) {
            spies.remove(event.getPlayer().getUniqueId());
        }

        if (hidden.contains(uuid)) {
            hidden.remove(event.getPlayer().getUniqueId());
        }
    }
}
