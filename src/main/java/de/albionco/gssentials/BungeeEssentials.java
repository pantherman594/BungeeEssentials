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
import de.albionco.gssentials.event.PlayerListener;
import de.albionco.gssentials.integration.IntegrationProvider;
import de.albionco.gssentials.integration.IntegrationTest;
import de.albionco.gssentials.regex.RuleManager;
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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class BungeeEssentials extends Plugin {
    private static BungeeEssentials instance;
    private Configuration config = null;
    private static Configuration configStat = null;
    private IntegrationProvider helper;
    private boolean watchMultiLog;
    private boolean shouldClean;
    private boolean joinAnnounce;
    private boolean commandSpy;
    private boolean integrated;
    private boolean chatRules;
    private boolean chatSpam;
    private File configFile;
    private static File configFileStat;
    private boolean useLog;
    private boolean rules;
    private boolean spam;
    
    public static String StaffChat_MAIN;
    public static String Alert_MAIN;
    public static String Find_MAIN;
    public static String Hide_MAIN;
    public static String Join_MAIN;
    public static String ServerList_MAIN;
    public static String Message_MAIN;
    public static String Reply_MAIN;
    public static String Send_MAIN;
    public static String SendAll_MAIN;
    public static String Slap_MAIN;
    public static String Spy_MAIN;
    public static String CSpy_MAIN;
    public static String Reload_MAIN;

    public static String[] StaffChat_ALIAS;
    public static String[] Alert_ALIAS;
    public static String[] Find_ALIAS;
    public static String[] Hide_ALIAS;
    public static String[] Join_ALIAS;
    public static String[] ServerList_ALIAS;
    public static String[] Message_ALIAS;
    public static String[] Reply_ALIAS;
    public static String[] Send_ALIAS;
    public static String[] SendAll_ALIAS;
    public static String[] Slap_ALIAS;
    public static String[] Spy_ALIAS;
    public static String[] CSpy_ALIAS;
    public static String[] Reload_ALIAS;

    public static BungeeEssentials getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        configFile = new File(getDataFolder(), "config.yml");
        configFileStat = new File(getDataFolder(), "config.yml");
        reload();
    }

    @Override
    public void onDisable() {
        Log.reset();
    }

    private void saveConfig() throws IOException {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            Files.copy(getResourceAsStream("config.yml"), file.toPath());
        }
    }

    private void loadConfig() throws IOException {
        if (!configFile.exists()) {
            saveConfig();
        }
        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        configStat = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFileStat);
    }

    public boolean reload() {
        try {
            loadConfig();
            Dictionary.load();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }

        ProxyServer.getInstance().getPluginManager().unregisterCommands(this);
        ProxyServer.getInstance().getPluginManager().unregisterListeners(this);

        Messenger.reset();
        Log.reset();
        watchMultiLog = false;
        chatRules = false;
        chatSpam = false;
        rules = false;
        spam = false;


        String[] TEMP_ALIAS;
        List<String> BASE;
        BASE = configStat.getStringList("commands.staffchat");
        StaffChat_MAIN = BASE.get(0);
        TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
        StaffChat_ALIAS = Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length);
        BASE = configStat.getStringList("commands.alert");
        Alert_MAIN = BASE.get(0);
        TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
        Alert_ALIAS = Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length);
        BASE = configStat.getStringList("commands.find");
        Find_MAIN = BASE.get(0);
        TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
        Find_ALIAS = Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length);
        BASE = configStat.getStringList("commands.hide");
        Hide_MAIN = BASE.get(0);
        TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
        Hide_ALIAS = Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length);
        BASE = configStat.getStringList("commands.join");
        Join_MAIN = BASE.get(0);
        TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
        Join_ALIAS = Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length);
        BASE = configStat.getStringList("commands.list");
        ServerList_MAIN = BASE.get(0);
        TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
        ServerList_ALIAS = Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length);
        BASE = configStat.getStringList("commands.message");
        Message_MAIN = BASE.get(0);
        TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
        Message_ALIAS = Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length);
        BASE = configStat.getStringList("commands.reply");
        Reply_MAIN = BASE.get(0);
        TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
        Reply_ALIAS = Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length);
        BASE = configStat.getStringList("commands.send");
        Send_MAIN = BASE.get(0);
        TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
        Send_ALIAS = Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length);
        BASE = configStat.getStringList("commands.sendall");
        SendAll_MAIN = BASE.get(0);
        TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
        SendAll_ALIAS = Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length);
        BASE = configStat.getStringList("commands.slap");
        Slap_MAIN = BASE.get(0);
        TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
        Slap_ALIAS = Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length);
        BASE = configStat.getStringList("commands.spy");
        Spy_MAIN = BASE.get(0);
        TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
        Spy_ALIAS = Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length);
        BASE = configStat.getStringList("commands.commandspy");
        CSpy_MAIN = BASE.get(0);
        TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
        CSpy_ALIAS = Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length);
        BASE = configStat.getStringList("commands.reload");
        Reload_MAIN = BASE.get(0);
        TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
        Reload_ALIAS = Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length);

        int commands = 0;
        List<String> enable = config.getStringList("enable");
        if (enable.contains("staffchat")) {
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
        if(enable.contains("join")) {
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
        if (enable.contains("commandspy")) {
            register(new CSpyCommand());
            commands++;
        }
        if (enable.contains("rules") || enable.contains("rules-chat")) {
            rules = enable.contains("rules");
            chatRules = enable.contains("rules-chat");
            RuleManager.load();
            if (rules) {
                getLogger().log(Level.INFO, "Enabled rules for private chat");
            }
            if (chatRules) {
                getLogger().log(Level.INFO, "Enabled rules for public chat");
            }
        }
        register(new ReloadCommand());

        if (enable.contains("spy") || enable.contains("hide")) {
            ProxyServer.getInstance().getPluginManager().registerListener(this, new Messenger());
        }

        if (enable.contains("spam") || enable.contains("rules") || enable.contains("multilog") || enable.contains("commandspy")) {
            ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerListener());
        }

        commandSpy = enable.contains("commandspy");
        useLog = enable.contains("log");
        if (useLog) {
            if (!Log.setup()) {
                getLogger().log(Level.WARNING, "Error enabling the chat logger!");
            }
        }
        spam = enable.contains("spam");
        if (spam) {
            getLogger().log(Level.INFO, "Enabled spam filter for public chat");
        }
        chatSpam = enable.contains("spam-chat");
        if (chatSpam) {
            getLogger().log(Level.INFO, "Enabled spam filter for private chat");
        }
        watchMultiLog = enable.contains("multilog");
        shouldClean = enable.contains("clean");
        joinAnnounce = enable.contains("joinannounce");
        getLogger().log(Level.INFO, "Registered {0} commands successfully", commands);
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
        for (String name : IntegrationProvider.getPlugins()) {
            if (ignoredPlugins.contains(name)) {
                continue;
            }
            if (ProxyServer.getInstance().getPluginManager().getPlugin(name) != null) {
                integrated = true;
                helper = IntegrationProvider.get(name);
                break;
            }
        }

        if (isIntegrated()) {
            getLogger().log(Level.INFO, "*** Integrating with \"{0}\" plugin ***", helper.getName());
        } else {
            if (ignore.length > 0) {
                getLogger().log(Level.INFO, "*** No supported plugins detected ***");
            }
        }

        ProxyServer.getInstance().getScheduler().schedule(this, new IntegrationTest(), 7, TimeUnit.SECONDS);
    }

    private void register(Command command) {
        ProxyServer.getInstance().getPluginManager().registerCommand(this, command);
    }

    public boolean shouldLog() {
        return this.useLog;
    }

    public boolean shouldWatchMultilog() {
        return this.watchMultiLog;
    }

    public boolean shouldClean() {
        return this.shouldClean;
    }

    public boolean shouldAnnounce() {
        return this.joinAnnounce;
    }

    public boolean shouldCommandSpy() {
        return this.commandSpy;
    }

    public Configuration getConfig() {
        return this.config;
    }

    public IntegrationProvider getIntegrationProvider() {
        return helper;
    }

    public boolean isIntegrated() {
        return integrated;
    }

    public boolean useRules() {
        return rules;
    }

    public boolean useSpamProtection() {
        return spam;
    }

    public boolean useChatSpamProtetion() {
        return chatSpam;
    }

    public boolean useChatRules() {
        return chatRules;
    }
}
