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
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

public class Updater {
    private static Plugin plugin = BungeeEssentials.getInstance();

    public static boolean update(boolean beta) {
        final int oldVersion = getVersionFromString(plugin.getDescription().getVersion());
        File path = new File(ProxyServer.getInstance().getPluginsFolder(), "BungeeEssentials.jar");

        try {
            String versionLink = "https://raw.githubusercontent.com/PantherMan594/BungeeEssentials/master/version.txt";
            URL url = new URL(versionLink);
            URLConnection con = url.openConnection();
            InputStreamReader isr = new InputStreamReader(con.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            String newVer = reader.readLine();
            String newBVer = reader.readLine();
            int newVersion;
            if (beta) {
                newVersion = getVersionFromString(newBVer);
            } else {
                newVersion = getVersionFromString(newVer);
            }
            reader.close();

            if(newVersion > oldVersion) {
                plugin.getLogger().log(Level.INFO, "Update found, downloading...");
                String dlLink = "https://github.com/PantherMan594/BungeeEssentials/releases/download/" + newVersion + "/BungeeEssentials.jar";
                url = new URL(dlLink);
                con = url.openConnection();
                InputStream in = con.getInputStream();
                FileOutputStream out = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size;
                while ((size = in.read(buffer)) != -1) {
                    out.write(buffer, 0, size);
                }
                out.close();
                in.close();
                plugin.getLogger().log(Level.INFO, "Succesfully updated plugin to v" + newVer);
                plugin.getLogger().log(Level.INFO, "Plugin disabling, restart the server to enable changes!");
                return true;
            } else {
                plugin.getLogger().log(Level.INFO, "You are running the latest version of BungeeEssentials (v" + oldVersion + ")!");
                updateConfig();
            }
        } catch(IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to download update, please update manually from http://www.spigotmc.org/resources/bungeeessentials.1488/");
            plugin.getLogger().log(Level.WARNING, "Please report this error message: ");
            e.printStackTrace();
        }
        return false;
    }

    private static int getVersionFromString(String from) {
        String result = from.replace(".", "");

        return result.isEmpty() ? 0 : Integer.parseInt(result);
    }

    public static void updateConfig() {
        Configuration config = BungeeEssentials.getInstance().getConfig();
        Configuration messages = BungeeEssentials.getInstance().getMessages();
        int oldVersion = getVersionFromString(config.getString("configversion"));
        final int newVersion = getVersionFromString(plugin.getDescription().getVersion());
        if (oldVersion == newVersion) {
            return;
        }
        if (oldVersion <= 243) {
            String muteEnabled = messages.getString("mute.enabled");
            String muteDisabled = messages.getString("mute.disabled");
            String muteError = messages.getString("mute.error");
            messages.set("mute.muted.enabled", muteEnabled);
            messages.set("mute.muted.disabled", muteDisabled);
            messages.set("mute.muted.error", muteError);
            messages.set("mute.muter.enabled", "&c{{ PLAYER }} is now muted!");
            messages.set("mute.muter.disabled", "&a{{ PLAYER }} is no longer muted!");
            messages.set("mute.muter.error", "&cHey, you can''t mute that player!");
            oldVersion = 244;
        }
        if (oldVersion == 244) {
            String muterExemptError = messages.getString("mute.muted.error");
            messages.set("mute.muted.exempt", muterExemptError);
            messages.set("mute.muted.error", "&7{{ PLAYER }} tried to chat while muted!");
        }
        plugin.getLogger().log(Level.INFO, "Config updated. You may edit new values to your liking.");
    }
}