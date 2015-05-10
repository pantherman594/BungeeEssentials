package de.albionco.gssentials.utils;

import de.albionco.gssentials.BungeeEssentials;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public static void update() {
        int oldVersion = getVersionFromString(plugin.getDescription().getVersion());
        File path = new File(ProxyServer.getInstance().getPluginsFolder(), "BungeeEssentials.jar");

        try {
            String versionLink = "https://raw.githubusercontent.com/Fireflies/BungeeEssentials/master/version.txt";
            URL url = new URL(versionLink);
            URLConnection con = url.openConnection();
            InputStreamReader isr = new InputStreamReader(con.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            String newVer = reader.readLine();
            int newVersion = getVersionFromString(newVer);
            reader.close();

            if(newVersion > oldVersion) {
                plugin.getLogger().log(Level.INFO, "Update found, downloading...");
                String dlLink = "https://github.com/Fireflies/BungeeEssentials/releases/download/" + newVer + "/BungeeEssentials.jar";
                url = new URL(dlLink);
                con = url.openConnection();
                InputStream in = con.getInputStream();
                FileOutputStream out = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size;
                while((size = in.read(buffer)) != -1) {
                    out.write(buffer, 0, size);
                }

                out.close();
                in.close();
                plugin.getLogger().log(Level.INFO, "Succesfully updated plugin to v" + newVer);
                plugin.getLogger().log(Level.INFO, "Reload/restart server to enable changes");
            }
        } catch(IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to auto-update", e);
        }
    }

    private static int getVersionFromString(String from) {
        String result = from.replace(".", "");

        return result.isEmpty() ? 0 : Integer.parseInt(result);
    }
}