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

package de.albionco.gssentials.utils;

import com.google.common.base.Preconditions;
import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.regex.RuleManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
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
    private static Set<UUID> cspies = new HashSet<>();
    private static Set<UUID> chatting = new HashSet<>();
    private static Set<UUID> globalChat = new HashSet<>();

    public static void sendMessage(CommandSender sender, ProxiedPlayer recipient, String msg) {
        String message = msg;
        if (recipient != null && !Messenger.isHidden(recipient)) {
            ProxiedPlayer player = null;
            if (sender instanceof ProxiedPlayer) {
                player = (ProxiedPlayer) sender;
                if (BungeeEssentials.getInstance().isIntegrated() && (BungeeEssentials.getInstance().getIntegrationProvider() != null && BungeeEssentials.getInstance().getIntegrationProvider().isMuted((ProxiedPlayer) sender))) {
                    sender.sendMessage(ChatColor.RED + "You are muted and cannot message other players!");
                    return;
                }
                if (!sender.hasPermission(Permissions.Admin.BYPASS_FILTER) && BungeeEssentials.getInstance().useRules()) {
                    List<RuleManager.MatchResult> results = RuleManager.matches(msg);
                    for (RuleManager.MatchResult result : results) {
                        if (result.matched()) {
                            Log.log(player, result.getRule(), ChatType.PRIVATE);
                            switch (result.getRule().getHandle()) {
                                case ADVERTISEMENT:
                                    sender.sendMessage(Dictionary.format(Dictionary.WARNINGS_ADVERTISING));
                                    ruleNotify(Dictionary.NOTIFY_ADVERTISEMENT, (ProxiedPlayer) sender, msg);
                                    return;
                                case CURSING:
                                    sender.sendMessage(Dictionary.format(Dictionary.WARNING_HANDLE_CURSING));
                                    ruleNotify(Dictionary.NOTIFY_CURSING, (ProxiedPlayer) sender, msg);
                                    return;
                                case REPLACE:
                                    if (result.getRule().getReplacement() != null && message != null) {
                                        Matcher matcher = result.getRule().getPattern().matcher(message);
                                        message = matcher.replaceAll(result.getRule().getReplacement());
                                    }
                                    ruleNotify(Dictionary.NOTIFY_REPLACE, (ProxiedPlayer) sender, msg);
                                    break;
                                case COMMAND:
                                    CommandSender console = ProxyServer.getInstance().getConsole();
                                    String command = result.getRule().getCommand();
                                    if (command != null) {
                                        ProxyServer.getInstance().getPluginManager().dispatchCommand(console, command.replace("{{ SENDER }}", sender.getName()));
                                    }
                                    ruleNotify(Dictionary.NOTIFY_COMMAND, (ProxiedPlayer) sender, msg);
                                    return;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }

            String server = player != null ? player.getServer().getInfo().getName() : "CONSOLE";
            if (player != null) {
                if (BungeeEssentials.getInstance().useSpamProtection() && !player.hasPermission(Permissions.Admin.BYPASS_FILTER)) {
                    if (sentMessages.get(player.getUniqueId()) != null) {
                        String last = sentMessages.get(player.getUniqueId());
                        if (compare(msg, last) > 0.85) {
                            sender.sendMessage(Dictionary.format(Dictionary.WARNING_LEVENSHTEIN_DISTANCE));
                            return;
                        }
                    }
                    sentMessages.put(player.getUniqueId(), msg);
                }

                if (!sender.hasPermission(Permissions.Admin.SPY_EXEMPT)) {
                    String spyMessage = Dictionary.format(Dictionary.SPY_MESSAGE, "SERVER", server, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message);
                    for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
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
        String message = filter(player, event.getMessage());

        if (message == null) {
            event.setCancelled(true);
        } else {
            event.setMessage(message);
        }
    }

    public static String filter(ProxiedPlayer player, String msg) {
        Preconditions.checkNotNull(player, "player null");
        String message = msg;

        if (BungeeEssentials.getInstance().isIntegrated() && (BungeeEssentials.getInstance().getIntegrationProvider() != null && BungeeEssentials.getInstance().getIntegrationProvider().isMuted(player))) {
            player.sendMessage(ChatColor.RED + "You are muted and cannot message other players!");
            return null;
        }
        if (!player.hasPermission(Permissions.Admin.BYPASS_FILTER) && BungeeEssentials.getInstance().useChatRules()) {
            List<RuleManager.MatchResult> results = RuleManager.matches(msg);
            for (RuleManager.MatchResult result : results) {
                if (result.matched()) {
                    Log.log(player, result.getRule(), ChatType.PUBLIC);
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

            if (BungeeEssentials.getInstance().useChatSpamProtection()) {
                if (chatMessages.get(player.getUniqueId()) != null && compare(msg, chatMessages.get(player.getUniqueId())) > 0.85) {
                    player.sendMessage(Dictionary.format(Dictionary.WARNING_LEVENSHTEIN_DISTANCE));
                    return null;
                }
                chatMessages.put(player.getUniqueId(), msg);
            }
        }
        return message;
    }

    public static void ruleNotify(String message, ProxiedPlayer player, String sentMessage) {
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            if (p.hasPermission(Permissions.Admin.RULE_NOTIFY)) {
                p.sendMessage(Dictionary.format(message, "PLAYER", player.getName()));
                p.sendMessage(ChatColor.GRAY + "Original Message: " + sentMessage);
            }
        }
    }

    public static UUID reply(ProxiedPlayer player) {
        return messages.get(player.getUniqueId());
    }

    public static boolean isSpy(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        return spies.contains(player.getUniqueId());
    }

    public static boolean isCSpy(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        return cspies.contains(player.getUniqueId());
    }

    public static boolean isHidden(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        return hidden.contains(player.getUniqueId());
    }

    public static boolean isChatting(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        return chatting.contains(player.getUniqueId());
    }

    public static boolean isGlobalChat(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        return globalChat.contains(player.getUniqueId());
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

    public static boolean enableSpy(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        if (!isSpy(player)) {
            spies.add(player.getUniqueId());
        }
        return isSpy(player);
    }

    public static boolean disableSpy(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        if (isSpy(player)) {
            spies.remove(player.getUniqueId());
        }
        return isSpy(player);
    }

    public static boolean toggleCSpy(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        if (isCSpy(player)) {
            cspies.remove(player.getUniqueId());
        } else {
            cspies.add(player.getUniqueId());
        }
        return isCSpy(player);
    }

    public static boolean enableCSpy(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        if (!isCSpy(player)) {
            cspies.add(player.getUniqueId());
        }
        return isCSpy(player);
    }

    public static boolean disableCSpy(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        if (isCSpy(player)) {
            cspies.remove(player.getUniqueId());
        }
        return isCSpy(player);
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

    public static boolean enableHidden(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        if (!isHidden(player)) {
            hidden.add(player.getUniqueId());
        }
        return isHidden(player);
    }

    public static boolean disableHidden(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        if (isHidden(player)) {
            hidden.remove(player.getUniqueId());
        }
        return isHidden(player);
    }

    public static boolean toggleStaffChat(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        if (isChatting(player)) {
            chatting.remove(player.getUniqueId());
        } else {
            chatting.add(player.getUniqueId());
        }
        return isChatting(player);
    }

    public static boolean enableStaffChat(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        if (!isChatting(player)) {
            chatting.add(player.getUniqueId());
        }
        return isChatting(player);
    }

    public static boolean disableStaffChat(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        if (isChatting(player)) {
            chatting.remove(player.getUniqueId());
        }
        return isChatting(player);
    }

    public static boolean toggleGlobalChat(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        if (isGlobalChat(player)) {
            globalChat.remove(player.getUniqueId());
        } else {
            globalChat.add(player.getUniqueId());
        }
        return isGlobalChat(player);
    }

    public static boolean enableGlobalChat(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        if (!isGlobalChat(player)) {
            globalChat.add(player.getUniqueId());
        }
        return isGlobalChat(player);
    }

    public static boolean disableGlobalChat(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        if (isGlobalChat(player)) {
            globalChat.remove(player.getUniqueId());
        }
        return isGlobalChat(player);
    }

    public static void savePlayers() {
        File playerFile = new File(BungeeEssentials.getInstance().getDataFolder(), "players.txt");
        try {
            PrintWriter writer = new PrintWriter(playerFile, "UTF-8");
            writer.println(hidden.toString());
            writer.println(spies.toString());
            writer.println(cspies.toString());
            writer.println(chatting.toString());
            writer.println(globalChat.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getPlayers() {
        File playerFile = new File(BungeeEssentials.getInstance().getDataFolder(), "players.txt");
        if (playerFile.exists()) {
            try {
                FileInputStream fstream = new FileInputStream(playerFile);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                String[] players;
                if ((strLine = br.readLine()) != null) {
                    strLine = strLine.replace("[", "").replace("]", "");
                    if (!strLine.equals("")) {
                        players = strLine.split(", ");
                        for (String uuidStr : players) {
                            UUID uuid = UUID.fromString(uuidStr);
                            hidden.add(uuid);
                        }
                    }
                }
                if ((strLine = br.readLine()) != null) {
                    strLine = strLine.replace("[", "").replace("]", "");
                    if (!strLine.equals("")) {
                        players = strLine.split(", ");
                        for (String uuidStr : players) {
                            UUID uuid = UUID.fromString(uuidStr);
                            spies.add(uuid);
                        }
                    }
                }
                if ((strLine = br.readLine()) != null) {
                    strLine = strLine.replace("[", "").replace("]", "");
                    if (!strLine.equals("")) {
                        players = strLine.split(", ");
                        for (String uuidStr : players) {
                            UUID uuid = UUID.fromString(uuidStr);
                            cspies.add(uuid);
                        }
                    }
                }
                if ((strLine = br.readLine()) != null) {
                    strLine = strLine.replace("[", "").replace("]", "");
                    if (!strLine.equals("")) {
                        players = strLine.split(", ");
                        for (String uuidStr : players) {
                            UUID uuid = UUID.fromString(uuidStr);
                            chatting.add(uuid);
                        }
                    }
                }
                if ((strLine = br.readLine()) != null) {
                    strLine = strLine.replace("[", "").replace("]", "");
                    if (!strLine.equals("")) {
                        players = strLine.split(", ");
                        for (String uuidStr : players) {
                            UUID uuid = UUID.fromString(uuidStr);
                            globalChat.add(uuid);
                        }
                    }
                }
                in.close();
                Files.deleteIfExists(playerFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
    public void logout(PlayerDisconnectEvent event) {
        if (BungeeEssentials.getInstance().shouldClean()) {
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

            if (cspies.contains(uuid)) {
                cspies.remove(event.getPlayer().getUniqueId());
            }

            if (hidden.contains(uuid)) {
                hidden.remove(event.getPlayer().getUniqueId());
            }
        }
    }

    public enum ChatType {
        PUBLIC,
        PRIVATE
    }
}
