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

package me.hadrondev;

import me.hadrondev.commands.CommandRegister;
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

    public static BungeeEssentials me;

    @Override
    public void onEnable() {
        me = this;

        Configuration config = null;
        try {
            saveConfig();
            config = loadConfig();
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "Exception thrown whilst loading config, unable to proceed: ", ex);
        }

        if(config != null) {

            List<String> nope = config.getStringList("enable");

            Messages.ALERT = config.getString("format.alert", "&8[&a+&8] &7{ALERT}");
            Messages.FIND = config.getString("format.find", "&e{PLAYER} &ais playing on &e{SERVER}");
            Messages.MESSAGE = config.getString("format.message", "&a({SERVER}) &7[{SENDER} » {RECIPIENT}] &f{MESSAGE}");
            Messages.SEND = config.getString("format.send", "&aSending &e{PLAYER} &ato server &e{SERVER}");

            Messages.GLIST_HEADER = config.getString("settings.glist.header", "&aServers:");
            Messages.GLIST_SERVER = config.getString("settings.glist.body", "&a- {SERVER} {DENSITY}");

            Messages.INVALID_ARGS = config.getString("errors.invalid", "&cInvalid arguments provided.");
            Messages.PLAYER_OFFLINE = config.getString("errors.offline", "&cSorry, that player is offline.");
            Messages.NO_PERMS = config.getString("errors.permissions", "&cYou do not have permission to do that.");
            Messages.NO_SLAP = config.getString("errors.slap", "&cYou are unworthy of slapping people.");

            for(CommandRegister registering : CommandRegister.values()) {
                if(nope != null) {
                    if (nope.contains(registering.getName())) {
                        getProxy().getPluginManager().registerCommand(this, registering.getCommand());
                    } else {
                        getLogger().info("Skipping registration of command \"" + registering.getName() + "\" as specified by the config.");
                    }
                } else {
                    getProxy().getPluginManager().registerCommand(this, registering.getCommand());
                }
            }
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
}
