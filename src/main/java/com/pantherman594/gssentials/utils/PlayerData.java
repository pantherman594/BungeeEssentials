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

import com.pantherman594.gssentials.BungeeEssentials;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by David on 12/05.
 *
 * @author David
 */
public class PlayerData {
    private Configuration config;
    private String uuid;
    private List<String> friends;
    private List<String> outRequests;
    private List<String> inRequests;
    private List<UUID> ignoreList;
    private boolean hidden;
    private boolean spy;
    private boolean cSpy;
    private boolean globalChat;
    private boolean staffChat;
    private boolean muted;
    private boolean msging;

    public PlayerData(String uuid) {
        File playerFile = new File(BungeeEssentials.getInstance().getDataFolder() + File.separator + "playerdata" + File.separator + uuid + ".yml");
        if (playerFile.exists()) {
            try {
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(playerFile);
            } catch (IOException e) {
                BungeeEssentials.getInstance().getLogger().warning("Unable to load " + uuid + "'s data.");
                return;
            }
            this.uuid = uuid;
            friends = new ArrayList<>();
            outRequests = new ArrayList<>();
            inRequests = new ArrayList<>();
            friends.addAll(config.getStringList("friends"));
            outRequests.addAll(config.getStringList("requests.out"));
            inRequests.addAll(config.getStringList("requests.in"));
            ignoreList = new ArrayList<>();
            for (String id : config.getStringList("ignorelist")) {
                ignoreList.add(UUID.fromString(id));
            }
            hidden = config.getBoolean("hidden");
            spy = config.getBoolean("spy");
            cSpy = config.getBoolean("cspy");
            globalChat = config.getBoolean("globalchat");
            staffChat = config.getBoolean("staffchat");
            muted = config.getBoolean("muted");
            msging = config.getBoolean("msging");
        }
    }

    public boolean save() {
        File playerFile = new File(BungeeEssentials.getInstance().getDataFolder() + File.separator + "playerdata" + File.separator + uuid + ".yml");
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(playerFile);
        } catch (IOException e) {
            BungeeEssentials.getInstance().getLogger().warning("Unable to save " + uuid + "'s data.");
            return false;
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
            BungeeEssentials.getInstance().getLogger().warning("Unable to save " + uuid + "'s data.");
            return false;
        }
        return true;
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

    public boolean isIgnored(UUID uuid) {
        return ignoreList.contains(uuid);
    }

    public void setIgnored(UUID uuid, boolean status) {
        if (status) {
            ignoreList.add(uuid);
        } else {
            ignoreList.remove(uuid);
        }
    }

    public boolean toggleIgnore(UUID uuid) {
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
        setHidden(!isSpy());
        return isSpy();
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
}