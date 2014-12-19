/*
 * Copyright (c) 2014 Connor Spencer Harries
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

package de.albionco.gssentials;

import de.albionco.gssentials.commands.*;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;

public class BungeeEssentials extends Plugin {

    private Configuration config = null;

    @Override
    public void onEnable() {
        try {
            saveConfig();
            config = loadConfig();
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "Exception thrown whilst loading config, unable to proceed: ", ex);
        }

        if (config != null) {
            try {
                Dictionary.load();
                getLogger().info("Successfully loaded configuration values!");
            } catch (IllegalAccessException e) {
                return;
            }

            List<String> enable = config.getStringList("enable");

            if(enable.contains("admin")) {
                register(new Admin());
            }

            if(enable.contains("alert")) {
                register(new Alert());
            }

            if(enable.contains("find")) {
                register(new Find());
            }

            if(enable.contains("list")) {
                register(new ServerList());
            }

            if(enable.contains("message")) {
                register(new Message());
                register(new Reply());
            }

            if(enable.contains("send")) {
                register(new Send());
                register(new SendAll());
            }

            if(enable.contains("slap")) {
                register(new Slap());
            }
        } else {
            getLogger().severe("Unable to load/save the config");
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveConfig() throws IOException {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            Files.copy(getResourceAsStream("config.yml"), file.toPath());
        }
    }

    private Configuration loadConfig() throws IOException {
        return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
    }

    private void register(Command command) {
        ProxyServer.getInstance().getPluginManager().registerCommand(this, command);
    }

    public Configuration getConfig() {
        return this.config;
    }

    public static BungeeEssentials getInstance() {
        return (BungeeEssentials) ProxyServer.getInstance().getPluginManager().getPlugin("BungeeEssentials");
    }
}
