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

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by David on 12/05.
 *
 * @author David
 */
@SuppressWarnings({"WeakerAccess", "ResultOfMethodCallIgnored"})
public class PlayerData {

    private static com.pantherman594.gssentials.database.PlayerData pD = BungeeEssentials.getInstance().getPlayerData();

    /**
     * Converts a player's PlayerData to SQL format.
     *
     * @param uuid The uuid of the player.
     */
    public static void convertPlayerData(String uuid) {
        File playerFile = new File(BungeeEssentials.getInstance().getDataFolder() + File.separator + "playerdata" + File.separator + uuid + ".yml");
        if (playerFile.exists()) {
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
    }
}