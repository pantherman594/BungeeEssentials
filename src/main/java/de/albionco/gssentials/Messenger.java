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
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
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
    private static HashMap<UUID, String> chatMessages = new HashMap<>();
    private static HashMap<UUID, String> sentMessages = new HashMap<>();
    private static HashMap<UUID, UUID> messages = new HashMap<>();
    private static Set<UUID> hidden = new HashSet<>();
    private static Set<UUID> spies = new HashSet<>();

    public static void sendMessage(CommandSender sender, ProxiedPlayer recipient, String message) {
        if (recipient != null && !Messenger.isHidden(recipient)) {
            ProxiedPlayer player = null;
            if (sender instanceof ProxiedPlayer) {
                player = (ProxiedPlayer) sender;
                if (BungeeEssentials.getInstance().isIntegrated() && (BungeeEssentials.getInstance().getIntegrationProvider() != null && BungeeEssentials.getInstance().getIntegrationProvider().isMuted((ProxiedPlayer) sender))) {
                    sender.sendMessage(ChatColor.RED + "You are muted and cannot message other players!");
                    return;
                }
                if (!sender.hasPermission(Permissions.Admin.BYPASS_FILTER) && BungeeEssentials.getInstance().useRules()) {
                    RuleManager.MatchResult result = RuleManager.matches(message);
                    if (result.matched()) {
                        Log.log(player, result.getRule(), ChatType.PRIVATE);
                        switch (result.getRule().getHandle()) {
                            case ADVERTISEMENT:
                                sender.sendMessage(Dictionary.format(Dictionary.WARNINGS_ADVERTISING));
                                return;
                            case CURSING:
                                sender.sendMessage(Dictionary.format(Dictionary.WARNING_HANDLE_CURSING));
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
            if (player != null) {
                if (BungeeEssentials.getInstance().useSpamProtection() && !player.hasPermission(Permissions.Admin.BYPASS_FILTER)) {
                    if (sentMessages.get(player.getUniqueId()) != null) {
                        String last = sentMessages.get(player.getUniqueId());
                        if (compare(message, last) > 0.85) {
                            sender.sendMessage(Dictionary.format(Dictionary.WARNING_LEVENSHTEIN_DISTANCE));
                            return;
                        }
                    }
                    sentMessages.put(player.getUniqueId(), message);
                }

                if (!sender.hasPermission(Permissions.Admin.SPY_EXEMPT)) {
                    String spyMessage = Dictionary.format(Dictionary.SPY_MESSAGE, "SERVER", server, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message);
                    for (ProxiedPlayer onlinePlayer : player.getServer().getInfo().getPlayers()) {
                        if (player.getUniqueId() != onlinePlayer.getUniqueId() && recipient.getUniqueId() != onlinePlayer.getUniqueId()) {
                            if (onlinePlayer.hasPermission(Permissions.Admin.SPY) && Messenger.isSpy(onlinePlayer)) {
                                onlinePlayer.sendMessage(spyMessage);
                            }
                        }
                    }
                }
                messages.put(recipient.getUniqueId(), player.getUniqueId());
            }
            sender.sendMessage(Dictionary.formatMsg(Dictionary.FORMAT_PRIVATE_MESSAGE, "SERVER", recipient.getServer().getInfo().getName(), "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));
            recipient.sendMessage(Dictionary.formatMsg(Dictionary.FORMAT_PRIVATE_MESSAGE, "SERVER", server, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_OFFLINE));
        }
    }

    public static void chat(ProxiedPlayer player, ChatEvent event) {
        Preconditions.checkNotNull(player, "player null");
        Preconditions.checkNotNull(event, "event null");
        String message = event.getMessage();

        if (BungeeEssentials.getInstance().useChatRules()) {
            RuleManager.MatchResult result = RuleManager.matches(message);
            if (result.matched()) {
                Log.log(player, result.getRule(), ChatType.PUBLIC);
                switch (result.getRule().getHandle()) {
                    case ADVERTISEMENT:
                        player.sendMessage(Dictionary.format(Dictionary.WARNINGS_ADVERTISING));
                        event.setCancelled(true);
                        return;
                    case CURSING:
                        player.sendMessage(Dictionary.format(Dictionary.WARNING_HANDLE_CURSING));
                        event.setCancelled(true);
                        return;
                    case REPLACE:
                        if (result.getRule().getReplacement() != null) {
                            Matcher matcher = result.getRule().getPattern().matcher(message);
                            if (matcher.matches()) {
                                message = matcher.replaceAll(result.getRule().getReplacement());
                                event.setMessage(message);
                            }
                        }
                        break;
                }
            }
        }

        if (BungeeEssentials.getInstance().useChatSpamProtetion()) {
            if (chatMessages.get(player.getUniqueId()) != null && compare(message, chatMessages.get(player.getUniqueId())) > 0.85) {
                event.setCancelled(true);
                player.sendMessage(Dictionary.format(Dictionary.WARNING_LEVENSHTEIN_DISTANCE));
                return;
            }
            chatMessages.put(player.getUniqueId(), message);
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
        chatMessages.clear();
        sentMessages.clear();
        messages.clear();
        hidden.clear();
        spies.clear();
    }

    private static double compare(String first, String second) {
        String longer = first, shorter = second;
        if (first.length() < second.length()) {
            longer = second;
            shorter = first;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
        }
        return (longerLength - getDistance(longer, shorter)) / (double) longerLength;
    }

    private static int getDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public static int howManyHidden() {
        return hidden.size();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void logout(PlayerDisconnectEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (sentMessages.containsKey(uuid)) {
            sentMessages.remove(uuid);
        }

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

    public enum ChatType {
        PUBLIC,
        PRIVATE
    }
}
