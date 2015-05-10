package de.albionco.gssentials;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David on 5/9/2015.
 *
 * @author David Shen
 */
public class Updater {
    private static Plugin plugin = BungeeEssentials.getInstance();

    public static void update() {
        int oldVersion = getVersionFromString(plugin.getDescription().getVersion());
        File path = BungeeEssentials.getInstance().getDataFolder();

        try {
            String versionLink = "https://github.com/Fireflies/BungeeEssentials/version.txt";
            URL url = new URL(versionLink);
            URLConnection con = url.openConnection();
            InputStreamReader isr = new InputStreamReader(con.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            String newVer = reader.readLine();
            int newVersion = getVersionFromString(newVer);
            reader.close();

            if(newVersion > oldVersion) {
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
                plugin.getLogger().log(Level.INFO, "Succesfully updated plugin to v" + newVersion);
                plugin.getLogger().log(Level.INFO, "Reload/restart server to enable changes");
            }
        } catch(IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to auto-update", e);
        }
    }

    private static int getVersionFromString(String from) {
        String result = "";
        Pattern pattern = Pattern.compile("d+");
        Matcher matcher = pattern.matcher(from);

        while(matcher.find()) {
            result += matcher.group();
        }

        return result.isEmpty() ? 0 : Integer.parseInt(result);
    }
}