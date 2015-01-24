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

package de.albionco.gssentials;

import com.google.common.base.Preconditions;
import de.albionco.gssentials.command.admin.*;
import de.albionco.gssentials.command.general.*;
import de.albionco.gssentials.integration.IntegrationProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class BungeeEssentials extends Plugin {
    private static BungeeEssentials instance;
    private String[] plugins = {"BungeeAdminTools", "BungeeSuite"};
    private Configuration config = null;
    private IntegrationProvider helper;
    private boolean integrated;

    public static BungeeEssentials getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        try {
            saveConfig();
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "Unable to save configuration file: ", ex);
            getLogger().log(Level.SEVERE, "Plugin loading aborted!");
            return;
        }

        reload();
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

    private void loadConfig() throws IOException {
        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
    }

    public boolean isIntegrated() {
        return integrated;
    }

    public boolean reload() {
        try {
            loadConfig();
            Dictionary.load();
        } catch (IOException e) {
            return false;
        } catch (IllegalAccessException e) {
            return false;
        }
        ProxyServer.getInstance().getPluginManager().unregisterCommands(this);

        int commands = 0;
        List<String> enable = config.getStringList("enable");
        if (enable.contains("admin")) {
            register(new ChatCommand());
            commands++;
        }

        if (enable.contains("alert")) {
            register(new AlertCommand());
            commands++;
        }

        if (enable.contains("find")) {
            register(new FindCommand());
            commands++;
        }

        if (enable.contains("hide")) {
            register(new HideCommand());
            commands++;
        }

        if (enable.contains("join")) {
            register(new JoinCommand());
            commands++;
        }

        if (enable.contains("list")) {
            register(new ServerListCommand());
            commands++;
        }

        if (enable.contains("message")) {
            register(new MessageCommand());
            register(new ReplyCommand());
            commands += 2;
        }

        if (enable.contains("send")) {
            register(new SendCommand());
            register(new SendAllCommand());
            commands += 2;
        }

        if (enable.contains("slap")) {
            register(new SlapCommand());
            commands++;
        }

        if (enable.contains("spy")) {
            register(new SpyCommand());
            commands++;
        }

        getLogger().log(Level.INFO, "Registered {0} commands successfully", commands);
        ProxyServer.getInstance().getPluginManager().registerListener(this, new Messenger());

        getLogger().log(Level.INFO, "Scanning for compatible mute plugins..");
        setupIntegration();

        return true;
    }

    public void setupIntegration(String... ignore) {
        Preconditions.checkNotNull(ignore);
        integrated = false;
        helper = null;
        if (ignore.length > 0) {
            getLogger().log(Level.INFO, "*** Rescanning for supported plugins ***");
        }
        List<String> ignoredPlugins = Arrays.asList(ignore);
        for (String name : plugins) {
            boolean found = ProxyServer.getInstance().getPluginManager().getPlugin(name) != null;
            if (found && !ignoredPlugins.contains(name)) {
                integrated = true;
                helper = IntegrationProvider.get(name);
                break;
            }
        }

        if (isIntegrated()) {
            getLogger().log(Level.INFO, "*** Integrating with \"{0}\" plugin ***", getIntegrationProvider().getName());
        } else {
            if (ignore.length > 0) {
                getLogger().log(Level.INFO, "*** No supported plugins detected ***");
            }
        }
    }

    private void register(Command command) {
        ProxyServer.getInstance().getPluginManager().registerCommand(this, command);
    }

    public Configuration getConfig() {
        return this.config;
    }

    public IntegrationProvider getIntegrationProvider() {
        return helper;
    }
}
