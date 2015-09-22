/*
 * BungeeEssentials: Full customization of a few necessary features for your server!
 * Copyright (C) 2015  David Shen (PantherMan594)
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

package com.pantherman594.gssentials.utils;

import com.google.common.base.Preconditions;
import com.pantherman594.gssentials.BungeeEssentials;
import com.pantherman594.gssentials.regex.RuleManager;
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

@SuppressWarnings("deprecation")
public class Messenger implements Listener {
    public static HashMap<UUID, String> sentMessages = new HashMap<>();
    public static HashMap<UUID, UUID> messages = new HashMap<>();
    public static HashMap<UUID, Set<UUID>> ignoreList = new HashMap<>();
    private static HashMap<UUID, String> chatMessages = new HashMap<>();
    private static Set<UUID> hidden = new HashSet<>();
    private static Set<UUID> spies = new HashSet<>();
    private static Set<UUID> cspies = new HashSet<>();
    private static Set<UUID> staffChat = new HashSet<>();
    private static Set<UUID> muted = new HashSet<>();
    private static Set<UUID> globalChat = new HashSet<>();

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

        if (isMutedF(player, msg)) {
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
            if (message != null) {
                for (String word : Dictionary.BANNED_LIST) {
                    String finalReg = "\\b(";
                    for (char l : word.toCharArray()) {
                        finalReg += l + "(\\W|\\d|_)*";
                    }
                    finalReg += ")";
                    if (!finalReg.equals("\\b()")) {
                        message = message.replaceAll(finalReg, Dictionary.BANNED_REPLACE);
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

    public static void ruleNotify(String notification, ProxiedPlayer player, String sentMessage) {
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            if (p.hasPermission(Permissions.Admin.NOTIFY)) {
                p.sendMessage(Dictionary.format(notification, "PLAYER", player.getName()));
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

    public static Integer hiddenNum() {
        int hiddenNum = 0;
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            if (isHidden(p)) {
                hiddenNum++;
            }
        }
        return hiddenNum;
    }

    public static boolean isStaffChat(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        return staffChat.contains(player.getUniqueId());
    }

    public static boolean isMuted(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        return muted.contains(player.getUniqueId());
    }

    public static boolean isMutedF(ProxiedPlayer player, String msg) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        BungeeEssentials bInst = BungeeEssentials.getInstance();
        if (!player.hasPermission(Permissions.Admin.MUTE_EXEMPT) && (muted.contains(player.getUniqueId()) || (bInst.isIntegrated() && bInst.getIntegrationProvider().isMuted(player)))) {
            player.sendMessage(Dictionary.format(Dictionary.MUTE_ERROR));
            ruleNotify(Dictionary.format(Dictionary.MUTE_ERRORN), player, msg);
            return true;
        }
        return false;
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
        if (isStaffChat(player)) {
            staffChat.remove(player.getUniqueId());
        } else {
            staffChat.add(player.getUniqueId());
        }
        return isStaffChat(player);
    }

    public static boolean toggleMute(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        if (isMuted(player)) {
            muted.remove(player.getUniqueId());
        } else {
            muted.add(player.getUniqueId());
        }
        return isMuted(player);
    }

    public static boolean enableStaffChat(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        if (!isStaffChat(player)) {
            staffChat.add(player.getUniqueId());
        }
        return isStaffChat(player);
    }

    public static boolean disableStaffChat(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "Invalid player specified");
        if (isStaffChat(player)) {
            staffChat.remove(player.getUniqueId());
        }
        return isStaffChat(player);
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
            writer.println(staffChat.toString());
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
                            staffChat.add(uuid);
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

    public static double compare(String first, String second) {
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

    public static void ignore(ProxiedPlayer ignorer, ProxiedPlayer ignored) {
        if (Messenger.ignoreList.containsKey(ignorer.getUniqueId())) {
            Set<UUID> ignoredList = Messenger.ignoreList.get(ignorer.getUniqueId());
            if (isIgnoring(ignorer, ignored)) {
                ignoredList.remove(ignored.getUniqueId());
                ignorer.sendMessage(Dictionary.format(Dictionary.IGNORE_DISABLED, "PLAYER", ignored.getName()));
            } else {
                ignoredList.add(ignored.getUniqueId());
                ignorer.sendMessage(Dictionary.format(Dictionary.IGNORE_ENABLED, "PLAYER", ignored.getName()));
            }
        } else {
            Set<UUID> ignoredList = new HashSet<>();
            ignoredList.add(ignored.getUniqueId());
            Messenger.ignoreList.put(ignorer.getUniqueId(), ignoredList);
            ignorer.sendMessage(Dictionary.format(Dictionary.IGNORE_ENABLED, "PLAYER", ignored.getName()));
        }
    }

    public static boolean isIgnoring(ProxiedPlayer ignorer, ProxiedPlayer ignored) {
        if (Messenger.ignoreList.containsKey(ignorer.getUniqueId())) {
            Set<UUID> ignoredList = Messenger.ignoreList.get(ignorer.getUniqueId());
            return (ignoredList.contains(ignored.getUniqueId()));
        } else return false;
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
