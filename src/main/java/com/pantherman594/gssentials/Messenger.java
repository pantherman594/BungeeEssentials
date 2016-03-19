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

import com.google.common.base.Preconditions;
import com.pantherman594.gssentials.regex.RuleManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;

public class Messenger implements Listener {
    public static Map<UUID, UUID> messages = new HashMap<>();
    private static Map<UUID, String> sentMessages = new HashMap<>();
    private static Map<UUID, String> chatMessages = new HashMap<>();

    public static void chat(ProxiedPlayer player, ChatEvent event) {
        Preconditions.checkNotNull(player, "player null");
        Preconditions.checkNotNull(event, "event null");
        String message = filter(player, event.getMessage(), ChatType.PUBLIC);

        if (message == null) {
            event.setCancelled(true);
        } else {
            event.setMessage(message);
        }
    }

    public static String filter(ProxiedPlayer player, String msg, ChatType ct) {
        if (msg == null || player == null) {
            return msg;
        }
        String message = msg;

        if (isMutedF(player, msg)) {
            return null;
        }
        if (!player.hasPermission(Permissions.Admin.BYPASS_FILTER)) {
            if (BungeeEssentials.getInstance().contains(ct.getRule())) {
                List<RuleManager.MatchResult> results = BungeeEssentials.getInstance().getRuleManager().matches(msg);
                for (RuleManager.MatchResult result : results) {
                    if (result.matched()) {
                        Log.log(player, result.getRule(), ct);
                        switch (result.getRule().getHandle()) {
                            case ADVERTISEMENT:
                                player.sendMessage(Dictionary.format(Dictionary.WARNINGS_ADVERTISING));
                                message = null;
                                ruleNotify(Dictionary.NOTIFY_ADVERTISEMENT, player, msg);
                                break;
                            case CURSING:
                                player.sendMessage(Dictionary.format(Dictionary.WARNING_HANDLE_CURSING));
                                message = null;
                                ruleNotify(Dictionary.NOTIFY_CURSING, player, msg);
                                break;
                            case REPLACE:
                                if (result.getRule().getReplacement() != null && message != null) {
                                    Matcher matcher = result.getRule().getPattern().matcher(message);
                                    message = matcher.replaceAll(result.getRule().getReplacement());
                                }
                                ruleNotify(Dictionary.NOTIFY_REPLACE, player, msg);
                                break;
                            case COMMAND:
                                CommandSender console = ProxyServer.getInstance().getConsole();
                                String command = result.getRule().getCommand();
                                if (command != null) {
                                    ProxyServer.getInstance().getPluginManager().dispatchCommand(console, command.replace("{{ SENDER }}", player.getName()));
                                }
                                ruleNotify(Dictionary.NOTIFY_COMMAND, player, msg);
                                message = null;
                                break;
                            default:
                                break;
                        }
                    }
                }
                message = filterBannedWords(player, message, msg);
            }
            if (BungeeEssentials.getInstance().contains(ct.getSpam())) {
                Map<UUID, String> msgs;
                if (ct.getSpam().equals("spam")) {
                    msgs = sentMessages;
                } else {
                    msgs = chatMessages;
                }
                if (msgs.get(player.getUniqueId()) != null && compare(msg, msgs.get(player.getUniqueId())) > 0.85) {
                    player.sendMessage(Dictionary.format(Dictionary.WARNING_LEVENSHTEIN_DISTANCE));
                    return null;
                }
                msgs.put(player.getUniqueId(), msg);
            }
        }
        return message;
    }

    private static String filterBannedWords(ProxiedPlayer player, String message, String msg) {
        if (message != null) {
            for (String word : BungeeEssentials.getInstance().getMessages().getStringList("bannedwords.list")) {
                String finalReg = "\\b(";
                char[] chars = word.toLowerCase().toCharArray();
                char[] chars2 = word.toUpperCase().toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    finalReg += "[" + chars[i] + chars2[i] + "]+" + "[\\W\\d_]*";
                }
                finalReg += ")";
                if (!finalReg.equals("\\b()")) {
                    String replacement = Dictionary.BANNED_REPLACE;
                    if (replacement.length() == 1) {
                        String origRepl = replacement;
                        while (replacement.length() < word.length()) {
                            replacement += origRepl;
                        }
                    }
                    replacement += " ";
                    String message2 = message.replaceAll(finalReg, replacement);
                    if (!message2.equals(message)) {
                        ruleNotify(Dictionary.NOTIFY_REPLACE, player, msg);
                        message = message2;
                    }
                }
            }
        }
        return message;
    }

    private static void ruleNotify(String notification, ProxiedPlayer player, String sentMessage) {
        ProxyServer.getInstance().getPlayers().stream().filter(p -> p.hasPermission(Permissions.Admin.NOTIFY)).forEach(p -> {
            p.sendMessage(Dictionary.format(notification, "PLAYER", player.getName()));
            p.sendMessage(ChatColor.GRAY + "Original Message: " + sentMessage);
        });
    }

    public static UUID reply(ProxiedPlayer player) {
        return messages.get(player.getUniqueId());
    }

    public static Integer hiddenNum() {
        int hiddenNum = 0;
        for (PlayerData pD : PlayerData.getDatas().values()) {
            if (pD.isHidden()) {
                hiddenNum++;
            }
        }
        return hiddenNum;
    }

    static boolean isMutedF(ProxiedPlayer player, String msg) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        BungeeEssentials bInst = BungeeEssentials.getInstance();
        if (!player.hasPermission(Permissions.Admin.MUTE_EXEMPT) && (PlayerData.getData(player.getUniqueId()).isMuted() || (bInst.isIntegrated() && bInst.getIntegrationProvider().isMuted(player)))) {
            player.sendMessage(Dictionary.format(Dictionary.MUTE_ERROR));
            ruleNotify(Dictionary.MUTE_ERRORN, player, msg);
            return true;
        }
        return false;
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

    public enum ChatType {
        PUBLIC("rules-chat", "spam-chat"),
        PRIVATE("rules", "spam"),
        STAFF("rules", "spam"),
        GLOBAL("rules-chat", "spam-chat");

        private String rule;
        private String spam;

        ChatType(String rule, String spam) {
            this.rule = rule;
            this.spam = spam;
        }

        public String getRule() {
            return rule;
        }

        public String getSpam() {
            return spam;
        }
    }
}
