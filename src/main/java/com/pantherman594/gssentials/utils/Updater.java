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

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

public class Updater {
    private static Plugin plugin = BungeeEssentials.getInstance();
    private static boolean newConf = false;

    public static void update(boolean beta) {
        int oldVersion = getVersionFromString(plugin.getDescription().getVersion());
        File path = new File(ProxyServer.getInstance().getPluginsFolder(), "BungeeEssentials.jar");

        try {
            String versionLink = "https://raw.githubusercontent.com/Fireflies/BungeeEssentials/master/version.txt";
            URL url = new URL(versionLink);
            URLConnection con = url.openConnection();
            InputStreamReader isr = new InputStreamReader(con.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            String newVer = reader.readLine();
            String newConfVer = reader.readLine();
            String newBVer = reader.readLine();
            String newBConfVer = reader.readLine();
            int newVersion;
            int newConfVersion;
            if (beta) {
                newVersion = getVersionFromString(newBVer);
                newConfVersion = getVersionFromString(newBConfVer);
            } else {
                newVersion = getVersionFromString(newVer);
                newConfVersion = getVersionFromString(newConfVer);
            }
            reader.close();

            if(newVersion > oldVersion) {
                if (newConfVersion > oldVersion) {
                    newConf = true;
                    plugin.getLogger().log(Level.WARNING, "Update found with a config change.");
                    plugin.getLogger().log(Level.WARNING, "Go to http://www.spigotmc.org/resources/bungeeessentials.1488/ to compare and update your config.");
                }
                plugin.getLogger().log(Level.INFO, "Update found, downloading...");
                String dlLink = "https://github.com/Fireflies/BungeeEssentials/releases/download/" + newVer + "/BungeeEssentials.jar";
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
                plugin.getLogger().log(Level.INFO, "Restart the server to enable changes");
            } else {
                plugin.getLogger().log(Level.INFO, "You are running the latest version of BungeeEssentials!");
            }
        } catch(IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to auto-update, please download from http://www.spigotmc.org/resources/bungeeessentials.1488/", e);
            plugin.getLogger().log(Level.SEVERE, "Error message: ", e);
        }
    }

    private static int getVersionFromString(String from) {
        String result = from.replace(".", "");

        return result.isEmpty() ? 0 : Integer.parseInt(result);
    }

    public static boolean hasConfigChange() {
        return newConf;
    }
}