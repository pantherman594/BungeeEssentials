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
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

class Updater {

    private static final String VERSION_LINK = "https://raw.githubusercontent.com/PantherMan594/BungeeEssentials/master/version.txt";

    private BungeeEssentials plugin = BungeeEssentials.getInstance();

    /**
     * Updater for the BungeeEssentials plugin.
     *
     * @param beta Whether to update to beta versions.
     * @return Whether update was successful.
     */
    boolean update(boolean beta) {
        final String oldVerDec = plugin.getDescription().getVersion();
        final int oldVersion = getVersionFromString(oldVerDec);
        File path = new File(ProxyServer.getInstance().getPluginsFolder(), "BungeeEssentials.jar");

        URLConnection con;
        try {
            URL url = new URL(VERSION_LINK);
            con = url.openConnection();
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Invalid version link. Please contact plugin author.");
            return false;
        }

        String newVerDec;
        try (
                InputStreamReader isr = new InputStreamReader(con.getInputStream());
                BufferedReader reader = new BufferedReader(isr)
        ) {

            String newVer = reader.readLine();
            String newBVer = reader.readLine();
            newVerDec = beta ? newBVer : newVer;
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Unable to read version from link. Please contact plugin author.");
            return false;
        }

        int newVersion = getVersionFromString(newVerDec);

        if (newVersion > oldVersion) {
            plugin.getLogger().log(Level.INFO, "Update found, downloading...");
            String dlLink = "https://github.com/PantherMan594/BungeeEssentials/releases/download/" + newVerDec + "/BungeeEssentials.jar";
            try {
                URL url = new URL(dlLink);
                con = url.openConnection();
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Invalid download link. Please contact plugin author.");
                return false;
            }

            try (
                    InputStream in = con.getInputStream();
                    FileOutputStream out = new FileOutputStream(path)
            ) {
                byte[] buffer = new byte[1024];
                int size;
                while ((size = in.read(buffer)) != -1) {
                    out.write(buffer, 0, size);
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to download update, please update manually from http://www.spigotmc.org/resources/bungeeessentials.1488/");
                plugin.getLogger().log(Level.WARNING, "Error message: ");
                e.printStackTrace();
                return false;
            }

            plugin.getLogger().log(Level.INFO, "Succesfully updated plugin to v" + newVerDec);
            plugin.getLogger().log(Level.INFO, "Plugin disabling, restart the server to enable changes!");
            return true;
        } else {
            plugin.getLogger().log(Level.INFO, "You are running the latest version of BungeeEssentials (v" + oldVerDec + ")!");
            updateConfig();
        }
        return false;
    }

    /**
     * Updates the config to the latest version. Automatically moves config items
     * if needed. If new ones do not exist, uses the default values.
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored", "unchecked"})
    private void updateConfig() {
        Configuration config = plugin.getConfig();
        Configuration messages = plugin.getMessages();
        int oldVersion = getVersionFromString(config.getString("configversion"));
        int newVersion = getVersionFromString(plugin.getDescription().getVersion());
        if (oldVersion == newVersion) {
            return;
        }
        if (oldVersion == 0) {
            if (!messages.getString("mute.muter.error").equals("")) {
                oldVersion = 244;
            } else {
                oldVersion = 243;
            }
        }
        try {
            File oldDir = new File(plugin.getDataFolder(), "old");
            if (!oldDir.exists()) {
                oldDir.mkdir();
            }
            File newConf = new File(plugin.getDataFolder(), "config.yml");
            File oldConf = new File(oldDir, "config_v" + oldVersion + ".yml");
            File newMess = new File(plugin.getDataFolder(), "messages.yml");
            File oldMess = new File(oldDir, "messages_v" + oldVersion + ".yml");
            if (!oldConf.exists()) {
                Files.copy(newConf.toPath(), oldConf.toPath());
            }
            if (!oldMess.exists()) {
                Files.copy(newMess.toPath(), oldMess.toPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> enabledList;
        switch (oldVersion) {
            case 243:
                String muteEnabled = messages.getString("mute.enabled");
                String muteDisabled = messages.getString("mute.disabled");
                String muteError = messages.getString("mute.error");
                messages.set("mute.enabled", null);
                messages.set("mute.disabled", null);
                messages.set("mute.error", null);
                messages.set("mute.muted.enabled", muteEnabled);
                messages.set("mute.muted.disabled", muteDisabled);
                messages.set("mute.muted.error", muteError);
                messages.set("mute.muter.enabled", "&c{{ PLAYER }} is now muted!");
                messages.set("mute.muter.disabled", "&a{{ PLAYER }} is no longer muted!");
                messages.set("mute.muter.error", "&cHey, you can't mute that player!");
            case 244:
                String muterExemptError = messages.getString("mute.muter.error");
                messages.set("mute.muter.exempt", muterExemptError);
                messages.set("mute.muter.error", "&7{{ PLAYER }} tried to chat while muted!");
            case 245:
                messages.set("bannedwords.replace", "****");
                messages.set("bannedwords.list", Arrays.asList("anal", "anus", "aroused", "asshole", "bastard", "bitch", "boob", "bugger", "cock", "cum", "cunt", "dafuq", "dick", "ffs", "fuck", "gay", "hentai", "homo", "homosexual", "horny", "intercourse", "jerk", "lesbian", "milf", "nigga", "nigger", "pedo", "penis", "piss", "prostitute", "pussy", "rape", "rapist", "retard", "sex", "shit", "slag", "slut", "sperm", "spunk", "testicle", "titt", "tosser", "twat", "vagina", "wanker", "whore", "wtf"));
            case 246:
            case 247:
                enabledList = config.getStringList("enable");
                enabledList.remove("multirelog");
                enabledList.add("autoredirect");
                enabledList.add("fastrelog");
                enabledList.add("friend");
                enabledList.add("spam-command");
                config.set("enable", enabledList);
                config.set("commands.friend", Arrays.asList("friend", "f"));
                List<Map<String, String>> section = (List<Map<String, String>>) config.getList("aliases");
                config.set("aliases", null);
                for (Map<String, String> map : section) {
                    Preconditions.checkNotNull(map);
                    Preconditions.checkArgument(!map.isEmpty());
                    Preconditions.checkNotNull(map.get("alias"), "invalid alias");
                    Preconditions.checkNotNull(map.get("commands"), "invalid commands");
                    config.set("aliases." + map.get("alias"), map.get("commands"));
                }
                messages.set("message.format", messages.get("format.message"));
                messages.set("message.enabled", "&aMessaging is now enabled!");
                messages.set("message.disabled", "&cMessaging is now disabled!");
                messages.set("format.message", null);
                if (messages.get("bannedwords.replace").equals("****")) {
                    messages.set("bannedwords.replace", "*");
                }
                messages.set("multilog", null);
                messages.set("friend.header", "&2Current Friends:");
                messages.set("friend.body", "- {{ NAME }} ({{ SERVER }})");
                messages.set("friend.new", "&aYou are now friends with {{ NAME }}!");
                messages.set("friend.old", "&aYou are already friends with {{ NAME }}!");
                messages.set("friend.remove", "&cYou are no longer friends with {{ NAME }}.");
                messages.set("friend.outrequests.header", "&2Outgoing Friend Requests:");
                messages.set("friend.outrequests.body", "- {{ NAME }}");
                messages.set("friend.outrequests.new", "&a{{ NAME }} has received your friend request.");
                messages.set("friend.outrequests.old", "&cYou already requested to be friends with {{ NAME }}. Please wait for a response!");
                messages.set("friend.outrequests.remove", "&cThe friend request to {{ NAME }} was removed.");
                messages.set("friend.inrequests.header", "&2Incoming Friend Requests:");
                messages.set("friend.inrequests.body", "- {{ NAME }}");
                messages.set("friend.inrequests.new", "&a{{ NAME }} would like to be your friend. /friend <add|remove> {{ NAME }} to accept or decline the request.");
                messages.set("friend.inrequests.remove", "&cThe friend request from {{ NAME }} was removed.");
                messages.set("errors.fastrelog", "&cPlease wait before reconnecting!");
                List<Map<String, String>> section2 = (List<Map<String, String>>) messages.getList("announcements");
                messages.set("announcements", null);
                String name = "annc";
                int num = 0;
                for (Map<String, String> map : section2) {
                    Preconditions.checkNotNull(map);
                    Preconditions.checkArgument(!map.isEmpty());
                    Preconditions.checkNotNull(map.get("delay"), "invalid delay");
                    Preconditions.checkNotNull(map.get("interval"), "invalid interval");
                    Preconditions.checkNotNull(map.get("message"), "invalid message");
                    messages.set("announcements." + name + num + ".delay", map.get("delay"));
                    messages.set("announcements." + name + num + ".interval", map.get("interval"));
                    messages.set("announcements." + name + num + ".message", map.get("message"));
                    num++;
                }
            case 250:
                messages.set("list.body", messages.getString("list.body").replace("{{ DENSITY }}", "({{ DENSITY }})"));
                messages.set("friend.body", messages.getString("friend.body") + "{{ HOVER: Click to join your friend! }}{{ CLICK: /server {{ SERVER }} }}");
                messages.set("friend.removeerror", "&cYou can't remove a friend you don't have!");
                messages.set("friend.inrequests.body", messages.getString("friend.inrequests.body") + "{{ HOVER: Click to accept friend request! }}{{ CLICK: /friend add {{ NAME }} }}");
                messages.set("friend.inrequests.new", messages.getString("friend.inrequests.new") + "{{ HOVER: Click to accept friend request! }}{{ CLICK: /friend add {{ NAME }} }}");
            case 251:
            case 252:
            case 253:
                enabledList = config.getStringList("enable");
                enabledList.add("server");
                config.set("enable", enabledList);
                if (messages.getString("list.header").equals("&aServers:")) {
                    messages.set("list.header", "You are on {{ CURRENT }}\n&aServers:");
                }
            case 254:
                String msgFormat = messages.getString("message.format");
                messages.set("message.format", null);
                messages.set("message.format.send", msgFormat.replace("{{ SENDER }}", "me"));
                messages.set("message.format.receive", msgFormat.replace("{{ RECIPIENT }}", "me"));
                messages.set("friend.removeerror", messages.get("friend.inrequests.removeerror"));
                messages.set("friend.inrequests.removeerror", null);
            case 255:
            case 256:
            case 257:
            case 258:
                PlayerData.convertPlayerData();
                enabledList = config.getStringList("enable");
                enabledList.add("hoverlist");
                enabledList.add("msggroup");
                enabledList.remove("clean");
                config.set("commands.msggroup", Arrays.asList("msggroup", "mg"));
                config.set("capspam.enabled", "true");
                config.set("capspam.percent", 50);
                config.set("aliases.mga", Collections.singletonList("msggroup admin {*}"));
                config.set("enable", enabledList);
                if (messages.getString("message.format.receive").equals("&7[{{ BREAK }}&7{{ SENDER }}{{ HOVER: On the {{ SERVER }} server. }}{{ BREAK }}&7 » me] &f{{ MESSAGE }}")) {
                    messages.set("message.format.receive", "&7[{{ BREAK }}&7{{ SENDER }}{{ HOVER: On the {{ SERVER }} server. }}{{ CLICK: SUG: /msg {{ SENDER }} }}{{ BREAK }}&7 » me] &f{{ MESSAGE }}{{ CLICK: SUG: /msg {{ SENDER }} }}");
                }
                messages.set("msggroup.format", "&9{{ NAME }} - {{ SENDER }} » &7{{ MESSAGE }}");
                messages.set("msggroup.create", "&aMessage group &f{{ NAME }} &asuccessfuly created! Invite players with /msggroup invite <username> {{ NAME }}!{{ HOVER: Click to prepare command. }}{{ CLICK: SUG: /msggroup invite <username> {{ NAME }} }}");
                messages.set("msggroup.join", "&aSuccessfully joined the &f{{ NAME }} &amessage group.");
                messages.set("msggroup.leave", "&aSuccessfully left the &f{{ NAME }} &amessage group.");
                messages.set("msggroup.invite.send", "&aSuccessfully invited &f{{ PLAYER }} &ato the &f{{ NAME }} &amessage group.");
                messages.set("msggroup.invite.receive", "&aYou've been invited to join the &f{{ NAME }} &amessage group. Click to accept!{{ CLICK: /msggroup join {{ NAME }} }}");
                messages.set("msggroup.kick.send", "&aSuccessfully kicked &f{{ PLAYER }} &afrom the &f{{ NAME }} &amessage group.");
                messages.set("msggroup.kick.receive", "&cYou've been kicked from the &f{{ NAME }} &cmessage group.");
                messages.set("msggroup.disband", "&aSuccessfully disbanded the &f{{ NAME }} &amessage group.");
                messages.set("msggroup.error.invalidname", "&cMessage group names must contain lowercase letters only, and must be at least 3 letters long.");
                messages.set("msggroup.error.nametaken", "&cSorry, that name has already been taken.");
                messages.set("msggroup.error.notinvited", "&cSorry, you can only join message groups with an invite.");
                messages.set("msggroup.error.notingroup", "&cSorry, you're not in that message group.");
                messages.set("msggroup.error.notexist", "&cSorry, that message group doesn't exist.");
                messages.set("msggroup.error.alreadyingroup", "&cWhoops, I think you're already in that group!");
                messages.set("msggroup.admin.listgroups.header", "&6Message Groups:");
                messages.set("msggroup.admin.listgroups.body", "&f- {{ NAME }}: {{ MEMBERS }}");
                messages.set("msggroup.admin.owner", "&a{{ PLAYER }} is now the owner of the {{ NAME }} message group.");
                messages.set("hoverlist.friend.order", "1");
                messages.set("hoverlist.friend.header", "&aFriends Online:");
                messages.set("hoverlist.staff.order", "2");
                messages.set("hoverlist.staff.header", "&6Staff Online:");
                messages.set("hoverlist.other.order", "0");
                messages.set("hoverlist.other.header", "&7Other Players:");
                if (messages.get("lookup.body").equals("&f - {{ PLAYER }}")) {
                    messages.set("lookup.body", "&f - {{ PLAYER }}{{ HOVER: Click to view player info }}{{ CLICK: /" + config.getStringList("commands.lookup").get(0) + " {{ PLAYER }} }}");
                }
                messages.set("lookup.player.header", "&6=====&l{{ PLAYER }}&6=====");
                messages.set("lookup.player.format", "&6{{ TYPE }}: &f{{ INFO }}{{ HOVER: Click to copy }}{{ CLICK: SUG: {{ INFO }} }}");
                if (messages.getString("errors.invalid").equals("&cInvalid arguments provided. Usage: {{ HELP }}")) {
                    messages.set("errors.invalid", "&cInvalid arguments provided. Usage: {{ HELP }}{{ HOVER: Click to fill in command }}{{ CLICK: SUG: {{ HELP }} }}");
                }
                if (messages.getString("errors.offline").equals("&cSorry, that player is offline.")) {
                    messages.set("errors.notfound", "&cSorry, no player was found.");
                } else {
                    messages.set("errors.notfound", messages.getString("errors.offline"));
                }
                messages.set("errors.offline", null);
                new File(plugin.getDataFolder(), "players.yml").delete();
            case 260:
                messages.set("msggroup.rename", "&aMessage group &f{{ OLDNAME }} &arenamed to &f{{ NAME }}&a.");
            case 261:
                if (messages.getString("message.format.receive").equals("&7[{{ BREAK }}&7{{ SENDER }}{{ HOVER: On the {{ SERVER }} server. }}{{ CLICK: SUG: /msg {{ SENDER }} }}{{ BREAK }}&7 » me] &f{{ MESSAGE }}{{ CLICK: SUG: /msg {{ SENDER }} }}")) {
                    messages.set("message.format.receive", "&7[{{ SENDER }} » me]{{ HOVER: On the {{ SERVER }} server. Click to respond. }}{{ CLICK: SUG: /msg {{ SENDER }}  }} &f{{ MESSAGE }}");
                }
            case 262:
                config.set("configversion", null);
                config.set("configversion", "2.6.3");
        }
        plugin.saveMainConfig();
        plugin.saveMessagesConfig();
        plugin.getLogger().log(Level.INFO, "Config updated. You may edit new values to your liking.");
    }

    /**
     * Converts a version string into a number (for version comparisons).
     *
     * @param from The string to convert into a version int.
     * @return The version in the form of an int.
     */
    private int getVersionFromString(String from) {
        String result = from.replace(".", "");

        return result.isEmpty() ? 0 : Integer.parseInt(result);
    }
}