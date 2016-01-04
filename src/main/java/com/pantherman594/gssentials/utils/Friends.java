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
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 12/05.
 *
 * @author David
 */
public class Friends {
    private Configuration config;
    private ProxiedPlayer player;
    private List<String> friends;
    private List<String> requests;

    public Friends(ProxiedPlayer p) {
        File playerFile = new File(BungeeEssentials.getInstance().getDataFolder() + File.separator + "playerdata" + File.separator + p.getUniqueId().toString() + ".yml");
        if (playerFile.exists()) {
            try {
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(playerFile);
            } catch (IOException e) {
                BungeeEssentials.getInstance().getLogger().warning("Unable to load " + p.getName() + "'s friends.");
                return;
            }
            player = p;
            friends = new ArrayList<>();
            requests = new ArrayList<>();
            friends.addAll(config.getStringList("friends"));
            requests.addAll(config.getStringList("requests"));
        }
    }

    public boolean save() {
        File playerFile = new File(BungeeEssentials.getInstance().getDataFolder() + File.separator + "playerdata" + File.separator + player.getUniqueId().toString() + ".yml");
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(playerFile);
        } catch (IOException e) {
            BungeeEssentials.getInstance().getLogger().warning("Unable to save " + player.getName() + "'s friends.");
            return false;
        }
        config.set("friends", friends);
        config.set("requests", requests);
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, playerFile);
        } catch (IOException e) {
            BungeeEssentials.getInstance().getLogger().warning("Unable to save " + player.getName() + "'s friends.");
            return false;
        }
        return true;
    }

    public List<String> getFriends() {
        return friends;
    }

    public List<String> getRequests() {
        return requests;
    }
}