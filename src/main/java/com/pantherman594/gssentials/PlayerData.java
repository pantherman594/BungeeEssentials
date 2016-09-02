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
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by David on 12/05.
 *
 * @author David
 */
@SuppressWarnings({"WeakerAccess", "ResultOfMethodCallIgnored", "UnusedParameters", "unused"})
public class PlayerData {

    private static com.pantherman594.gssentials.database.PlayerData pD = BungeeEssentials.getInstance().getPlayerData();

    private String uuid;

    /**
     * @deprecated
     */
    @Deprecated
    public PlayerData(String uuid, String name) {
        this.uuid = uuid;
        BungeeEssentials.getInstance().getLogger().log(Level.SEVERE, "Please update any plugin that uses BungeeEssentials' old PlayerData. " +
                "This will NOT be supported in future versions, and the plugin MAY NOT work correctly.");
    }

    /**
     * Converts a all players' PlayerData to SQL format.
     */
    public static void convertPlayerData() {
        com.pantherman594.gssentials.database.PlayerData pD = new com.pantherman594.gssentials.database.PlayerData();
        pD.createDataNotExist("CONSOLE");
        File playerDir = new File(BungeeEssentials.getInstance().getDataFolder() + File.separator + "playerdata");
        if (playerDir.exists()) {
            for (File playerFile : playerDir.listFiles()) {
                if (!playerFile.getName().endsWith(".yml")) {
                    continue;
                }
                String uuid = playerFile.getName().substring(0, playerFile.getName().length() - 4);
                Configuration config;
                try {
                    config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(playerFile);
                } catch (IOException e) {
                    BungeeEssentials.getInstance().getLogger().warning("Unable to load " + uuid + "'s data.");
                    return;
                }
                for (String friend : config.getStringList("friends")) {
                    pD.addFriend(uuid, friend);
                }
                for (String request : config.getStringList("requests.out")) {
                    pD.addOutRequest(uuid, request);
                }
                for (String request : config.getStringList("requests.in")) {
                    pD.addInRequest(uuid, request);
                }
                for (String ignore : config.getStringList("ignorelist")) {
                    pD.setIgnored(uuid, ignore, true);
                }
                pD.setHidden(uuid, config.getBoolean("hidden"));
                pD.setSpy(uuid, config.getBoolean("spy"));
                pD.setCSpy(uuid, config.getBoolean("cspy"));
                pD.setGlobalChat(uuid, config.getBoolean("globalchat"));
                pD.setStaffChat(uuid, config.getBoolean("staffchat"));
                pD.setMuted(uuid, config.getBoolean("muted"));
                pD.setMsging(uuid, config.getBoolean("msging"));
            }
            File playerDirArchive = new File(BungeeEssentials.getInstance().getDataFolder() + File.separator + "playerdata_old");
            try {
                Files.move(playerDir.toPath(), playerDirArchive.toPath());
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static Map<String, PlayerData> getDatas() {
        Map<String, PlayerData> playerDataList = new HashMap<>();
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            playerDataList.put(p.getUniqueId().toString(), new PlayerData(p.getUniqueId().toString(), null));
        }
        return playerDataList;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static PlayerData getData(String uuid) {
        return new PlayerData(uuid, null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static PlayerData getData(UUID uuid) {
        return getData(uuid.toString());
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static void setData(String uuid, PlayerData playerData) {
        // No easy way to convert to new version, plugins using this MUST UPDATE.
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static void clearData() {
        // No use in converting this
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean save() {
        // No easy way to convert to new version, plugins using this MUST UPDATE.
        return false;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public List<String> getFriends() {
        Set<String> friends = pD.getFriends(uuid);
        return Arrays.asList(friends.toArray(new String[friends.size()]));
    }

    /**
     * @deprecated
     */
    @Deprecated
    public List<String> getOutRequests() {
        Set<String> outRequests = pD.getOutRequests(uuid);
        return Arrays.asList(outRequests.toArray(new String[outRequests.size()]));
    }

    /**
     * @deprecated
     */
    @Deprecated
    public List<String> getInRequests() {
        Set<String> inRequests = pD.getInRequests(uuid);
        return Arrays.asList(inRequests.toArray(new String[inRequests.size()]));
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean isMuted() {
        return pD.isMuted(uuid);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setMuted(boolean muted) {
        pD.setMuted(uuid, muted);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean toggleMuted() {
        return pD.toggleMuted(uuid);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean isIgnored(String uuid) {
        return pD.isIgnored(this.uuid, uuid);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setIgnored(String uuid, boolean status) {
        pD.setIgnored(this.uuid, uuid, status);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean toggleIgnore(String uuid) {
        return pD.toggleIgnore(this.uuid, uuid);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean isHidden() {
        return pD.isHidden(uuid);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setHidden(boolean hidden) {
        pD.setHidden(uuid, hidden);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean toggleHidden() {
        return pD.toggleHidden(uuid);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean isSpy() {
        return pD.isSpy(uuid);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setSpy(boolean spy) {
        pD.setSpy(uuid, spy);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean toggleSpy() {
        return pD.toggleSpy(uuid);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean isCSpy() {
        return pD.isCSpy(uuid);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setCSpy(boolean cSpy) {
        pD.setCSpy(uuid, cSpy);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean toggleCSpy() {
        return pD.toggleCSpy(uuid);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean isGlobalChat() {
        return pD.isGlobalChat(uuid);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setGlobalChat(boolean globalChat) {
        pD.setGlobalChat(uuid, globalChat);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean toggleGlobalChat() {
        return pD.toggleGlobalChat(uuid);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean isStaffChat() {
        return pD.isStaffChat(uuid);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setStaffChat(boolean staffChat) {
        pD.setStaffChat(uuid, staffChat);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean toggleStaffChat() {
        return pD.toggleStaffChat(uuid);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean isMsging() {
        return pD.isMsging(uuid);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setMsging(boolean msging) {
        pD.setMsging(uuid, msging);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String getName() {
        return pD.getName(uuid);
    }
}