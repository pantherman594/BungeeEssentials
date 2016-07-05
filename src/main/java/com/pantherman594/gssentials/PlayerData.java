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

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by David on 12/05.
 *
 * @author David
 */
@SuppressWarnings({"WeakerAccess", "ResultOfMethodCallIgnored"})
public class PlayerData {
    private static Map<String, PlayerData> playerDataList;
    private Configuration config;
    private String name;
    private String uuid;
    private String ip;
    private List<String> friends;
    private List<String> outRequests;
    private List<String> inRequests;
    private List<String> ignoreList;
    private boolean hidden;
    private boolean spy;
    private boolean cSpy;
    private boolean globalChat;
    private boolean staffChat;
    private boolean muted;
    private boolean msging;

    /**
     * Registers a new player's PlayerData.
     *
     * @param uuid The uuid of the player.
     * @param name The player's name (if the player is online, to update the 'lastname' value).
     */
    public PlayerData(String uuid, String name) {
        friends = new ArrayList<>();
        outRequests = new ArrayList<>();
        inRequests = new ArrayList<>();
        ignoreList = new ArrayList<>();
        if (name != null) {
            this.name = name;
        }
        this.uuid = uuid;
        File playerFile = new File(BungeeEssentials.getInstance().getDataFolder() + File.separator + "playerdata" + File.separator + uuid + ".yml");
        if (playerFile.exists()) {
            try {
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(playerFile);
            } catch (IOException e) {
                if (name == null) {
                    BungeeEssentials.getInstance().getLogger().warning("Unable to load " + uuid + "'s data.");
                } else {
                    BungeeEssentials.getInstance().getLogger().warning("Unable to load " + name + "'s data.");
                }
                return;
            }
            if (name == null) {
                this.name = config.getString("lastname");
            }
            ip = config.getString("ip");
            friends.addAll(config.getStringList("friends"));
            outRequests.addAll(config.getStringList("requests.out"));
            inRequests.addAll(config.getStringList("requests.in"));
            ignoreList.addAll(config.getStringList("ignorelist"));
            hidden = config.getBoolean("hidden");
            spy = config.getBoolean("spy");
            cSpy = config.getBoolean("cspy");
            globalChat = config.getBoolean("globalchat");
            staffChat = config.getBoolean("staffchat");
            muted = config.getBoolean("muted");
            msging = config.getBoolean("msging");
        } else {
            hidden = false;
            spy = false;
            cSpy = false;
            globalChat = false;
            staffChat = false;
            muted = false;
            msging = true;
        }
        playerDataList.put(uuid, this);
    }

    /**
     * @return All the registered PlayerDatas
     */
    public static Map<String, PlayerData> getDatas() {
        return playerDataList;
    }

    /**
     * Get the PlayerData of a player. If it does not
     * exists, create it.
     *
     * @param uuid The uuid of the player as a string.
     * @return The PlayerData that matches the player's uuid.
     */
    public static PlayerData getData(String uuid) {
        return getDatas().containsKey(uuid) ? getDatas().get(uuid) : new PlayerData(uuid, null);
    }

    /**
     * Get the PlayerData of a player. If it does not
     * exists, create it.
     *
     * @param uuid The uuid of the player.
     * @return The PlayerData that matches the player's uuid.
     */
    public static PlayerData getData(UUID uuid) {
        return getData(uuid.toString());
    }

    /**
     * Sets a player's PlayerData.
     *
     * @param uuid       The uuid of the player.
     * @param playerData The PlayerData to add to the list.
     */
    public static void setData(String uuid, PlayerData playerData) {
        playerDataList.put(uuid, playerData);
    }

    /**
     * Clears the registered PlayerDatas.
     */
    public static void clearData() {
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            getData(p.getUniqueId()).save();
        }
        playerDataList = new HashMap<>();
    }

    /**
     * Saves a PlayerData to file.
     *
     * @return Whether save was successful.
     */
    public boolean save() {
        File playerDir = new File(BungeeEssentials.getInstance().getDataFolder(), "playerdata");
        File playerFile = new File(playerDir, uuid + ".yml");
        try {
            if (!playerDir.exists()) {
                playerDir.mkdir();
            }
            if (!playerFile.exists()) {
                playerFile.createNewFile();
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(playerFile);
        } catch (IOException e) {
            BungeeEssentials.getInstance().getLogger().warning("Unable to save " + name + "'s data.");
            return false;
        }
        config.set("lastname", name);
        if (BungeeEssentials.getInstance().getProxy().getPlayer(uuid) != null) {
            config.set("ip", BungeeEssentials.getInstance().getProxy().getPlayer(uuid).getAddress().getAddress().getHostAddress());
        }
        config.set("friends", friends);
        config.set("requests.out", outRequests);
        config.set("requests.in", inRequests);
        config.set("ignorelist", ignoreList);
        config.set("hidden", hidden);
        config.set("spy", spy);
        config.set("cspy", cSpy);
        config.set("globalchat", globalChat);
        config.set("staffchat", staffChat);
        config.set("muted", muted);
        config.set("msging", msging);
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, playerFile);
        } catch (IOException e) {
            BungeeEssentials.getInstance().getLogger().warning("Unable to save " + name + "'s data.");
            return false;
        }
        playerDataList.remove(uuid);
        return true;
    }

    public String getIp() {
        return ip;
    }

    public List<String> getFriends() {
        return friends;
    }

    public List<String> getOutRequests() {
        return outRequests;
    }

    public List<String> getInRequests() {
        return inRequests;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean toggleMuted() {
        setMuted(!isMuted());
        return isMuted();
    }

    public boolean isIgnored(String uuid) {
        return ignoreList.contains(uuid);
    }

    public void setIgnored(String uuid, boolean status) {
        if (status) {
            ignoreList.add(uuid);
        } else {
            ignoreList.remove(uuid);
        }
    }

    public boolean toggleIgnore(String uuid) {
        setIgnored(uuid, !isIgnored(uuid));
        return isIgnored(uuid);
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean toggleHidden() {
        setHidden(!isHidden());
        return isHidden();
    }

    public boolean isSpy() {
        return spy;
    }

    public void setSpy(boolean spy) {
        this.spy = spy;
    }

    public boolean toggleSpy() {
        setSpy(!isSpy());
        return isSpy();
    }

    public boolean isCSpy() {
        return cSpy;
    }

    public void setCSpy(boolean cSpy) {
        this.cSpy = cSpy;
    }

    public boolean toggleCSpy() {
        setCSpy(!isCSpy());
        return isCSpy();
    }

    public boolean isGlobalChat() {
        return globalChat;
    }

    public void setGlobalChat(boolean globalChat) {
        this.globalChat = globalChat;
    }

    public boolean toggleGlobalChat() {
        setGlobalChat(!isGlobalChat());
        return isGlobalChat();
    }

    public boolean isStaffChat() {
        return staffChat;
    }

    public void setStaffChat(boolean staffChat) {
        this.staffChat = staffChat;
    }

    public boolean toggleStaffChat() {
        setStaffChat(!isStaffChat());
        return isStaffChat();
    }

    public boolean isMsging() {
        return msging;
    }

    public void setMsging(boolean msging) {
        this.msging = msging;
    }

    public String getName() {
        return name;
    }
}