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

import de.albionco.gssentials.BungeeEssentials;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

/**
 * Created by David on 5/9/2015.
 *
 * @author David Shen
 */
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