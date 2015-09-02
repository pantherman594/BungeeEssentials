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

package com.pantherman594.gssentials;

import com.google.common.base.Preconditions;
import com.pantherman594.gssentials.aliases.AliasManager;
import com.pantherman594.gssentials.announcement.AnnouncementManager;
import com.pantherman594.gssentials.command.admin.*;
import com.pantherman594.gssentials.command.general.*;
import com.pantherman594.gssentials.event.PlayerListener;
import com.pantherman594.gssentials.integration.IntegrationProvider;
import com.pantherman594.gssentials.integration.IntegrationTest;
import com.pantherman594.gssentials.regex.RuleManager;
import com.pantherman594.gssentials.utils.Dictionary;
import com.pantherman594.gssentials.utils.Log;
import com.pantherman594.gssentials.utils.Messenger;
import com.pantherman594.gssentials.utils.Updater;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class BungeeEssentials extends Plugin {
    public static HashMap<String, String> mainList = new HashMap<>();
    public static HashMap<String, String[]> aliasList = new HashMap<>();
    private static BungeeEssentials instance;
    private Configuration config = null;
    private Configuration messages = null;
    private Configuration players = null;
    private IntegrationProvider helper;
    private boolean watchMultiLog;
    private boolean shouldClean;
    private boolean joinAnnounce;
    private boolean commandSpy;
    private boolean integrated;
    private boolean chatRules;
    private boolean chatSpam;
    private boolean ignore;
    private boolean mute;
    private File configFile;
    private File messageFile;
    private File playerFile;
    private boolean useLog;
    private boolean logAll;
    private boolean rules;
    private boolean spam;

    public static BungeeEssentials getInstance() {
        return instance;
    }

    public static String getMain(String key) {
        return mainList.get(key);
    }

    public static String[] getAlias(String key) {
        return aliasList.get(key);
    }

    @Override
    public void onEnable() {
        instance = this;
        configFile = new File(getDataFolder(), "config.yml");
        messageFile = new File(getDataFolder(), "messages.yml");
        playerFile = new File(getDataFolder(), "players.yml");
        try {
            loadConfig();
        } catch (Exception ignored) {
        }
        if (getConfig().getStringList("enable").contains("updater")) {
            boolean updated;
            if (getConfig().getStringList("enable").contains("betaupdates")) {
                updated = Updater.update(true);
            } else {
                updated = Updater.update(false);
            }
            if (updated) {
                return;
            }
        }
        reload();
        Messenger.getPlayers();
    }

    @Override
    public void onDisable() {
        Log.reset();
        Messenger.savePlayers();
    }

    private void saveConfig() throws IOException {
        if (!getDataFolder().exists()) {
            if (! getDataFolder().mkdir()) {
                getLogger().log(Level.WARNING, "Unable to create config folder!");
            }
        }
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            Files.copy(getResourceAsStream("config.yml"), file.toPath());
        }
        file = new File(getDataFolder(), "players.yml");
        if (!file.exists()) {
            Files.copy(getResourceAsStream("players.yml"), file.toPath());
        }
        file = new File(getDataFolder(), "messages.yml");
        if (!file.exists()) {
            Files.copy(getResourceAsStream("messages.yml"), file.toPath());
        }
    }

    private void loadConfig() throws IOException {
        if (!configFile.exists()) {
            saveConfig();
        }
        if (!messageFile.exists()) {
            saveConfig();
        }
        if (!playerFile.exists()) {
            saveConfig();
        }
        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        messages = ConfigurationProvider.getProvider(YamlConfiguration.class).load(messageFile);
        players = ConfigurationProvider.getProvider(YamlConfiguration.class).load(playerFile);
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

        Log.reset();
        watchMultiLog = false;
        chatRules = false;
        chatSpam = false;
        rules = false;
        spam = false;

        int commands = 0;
        List<String> BASE;
        String[] TEMP_ALIAS;
        List<String> enable = config.getStringList("enable");
        if (enable.contains("alert")) {
            BASE = config.getStringList("commands.alert");
            if (BASE.isEmpty()) {
                getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
                getLogger().log(Level.WARNING, "Falling back to default value for key commands.alert");
                BASE = Arrays.asList("alert", "broadcast");
            }
            mainList.put("alert", BASE.get(0));
            TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
            aliasList.put("alert", Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length));
            register(new AlertCommand());
            commands++;
        }
        boolean announcement = enable.contains("announcement");
        if (announcement) {
            AnnouncementManager.load();
            getLogger().log(Level.INFO, "Enabled announcements");
        }
        commandSpy = enable.contains("commandspy");
        if (commandSpy) {
            BASE = config.getStringList("commands.commandspy");
            if (BASE.isEmpty()) {
                getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
                getLogger().log(Level.WARNING, "Falling back to default value for key commands.commandspy");
                BASE = Arrays.asList("commandspy", "cspy");
            }
            mainList.put("commandspy", BASE.get(0));
            TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
            aliasList.put("commandspy", Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length));
            register(new CSpyCommand());
            commands++;
        }
        if (enable.contains("hide")) {
            BASE = config.getStringList("commands.hide");
            if (BASE.isEmpty()) {
                getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
                getLogger().log(Level.WARNING, "Falling back to default value for key commands.hide");
                BASE = Arrays.asList("hide", "");
            }
            mainList.put("hide", BASE.get(0));
            TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
            aliasList.put("hide", Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length));
            register(new HideCommand());
            commands++;
        }
        joinAnnounce = enable.contains("joinannounce");
        if (enable.contains("lookup")) {
            BASE = config.getStringList("commands.lookup");
            if (BASE.isEmpty()) {
                getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
                getLogger().log(Level.WARNING, "Falling back to default value for key commands.lookup");
                BASE = Arrays.asList("lookup", "");
            }
            mainList.put("lookup", BASE.get(0));
            TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
            aliasList.put("lookup", Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length));
            register(new LookupCommand());
            commands++;
        }
        mute = enable.contains("mute");
        if (mute) {
            BASE = config.getStringList("commands.mute");
            if (BASE.isEmpty()) {
                getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
                getLogger().log(Level.WARNING, "Falling back to default value for key commands.mute");
                BASE = Arrays.asList("bmute", "");
            }
            mainList.put("mute", BASE.get(0));
            TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
            aliasList.put("mute", Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length));
            register(new MuteCommand());
            commands++;
        }
        if (enable.contains("send")) {
            BASE = config.getStringList("commands.send");
            if (BASE.isEmpty()) {
                getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
                getLogger().log(Level.WARNING, "Falling back to default value for key commands.send");
                BASE = Arrays.asList("send", "");
            }
            mainList.put("send", BASE.get(0));
            TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
            aliasList.put("send", Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length));
            BASE = config.getStringList("commands.sendall");
            if (BASE.isEmpty()) {
                getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
                getLogger().log(Level.WARNING, "Falling back to default value for key commands.sendall");
                BASE = Arrays.asList("sendall", "");
            }
            mainList.put("sendall", BASE.get(0));
            TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
            aliasList.put("sendall", Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length));
            register(new SendCommand());
            register(new SendAllCommand());
            commands += 2;
        }
        if (enable.contains("spy")) {
            BASE = config.getStringList("commands.spy");
            if (BASE.isEmpty()) {
                getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
                getLogger().log(Level.WARNING, "Falling back to default value for key commands.spy");
                BASE = Arrays.asList("spy", "socialspy");
            }
            mainList.put("spy", BASE.get(0));
            TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
            aliasList.put("spy", Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length));
            register(new SpyCommand());
            commands++;
        }
        if (enable.contains("staffchat")) {
            BASE = config.getStringList("commands.staffchat");
            if (BASE.isEmpty()) {
                getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
                getLogger().log(Level.WARNING, "Falling back to default value for key commands.staffchat");
                BASE = Arrays.asList("staffchat","admin","a","sc");
            }
            mainList.put("staffchat", BASE.get(0));
            TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
            aliasList.put("staffchat", Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length));
            register(new StaffChatCommand());
            commands++;
        }
        if (enable.contains("chat")) {
            BASE = config.getStringList("commands.chat");
            if (BASE.isEmpty()) {
                getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
                getLogger().log(Level.WARNING, "Falling back to default value for key commands.chat");
                BASE = Arrays.asList("g", "global");
            }
            mainList.put("chat", BASE.get(0));
            TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
            aliasList.put("chat", Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length));
            register(new ChatCommand());
            commands++;
        }
        if (enable.contains("find")) {
            BASE = config.getStringList("commands.find");
            if (BASE.isEmpty()) {
                getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
                getLogger().log(Level.WARNING, "Falling back to default value for key commands.find");
                BASE = Arrays.asList("find","whereis");
            }
            mainList.put("find", BASE.get(0));
            TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
            aliasList.put("find", Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length));
            register(new FindCommand());
            commands++;
        }
        ignore = enable.contains("ignore");
        if (ignore) {
            BASE = config.getStringList("commands.ignore");
            if (BASE.isEmpty()) {
                getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
                getLogger().log(Level.WARNING, "Falling back to default value for key commands.ignore");
                BASE = Arrays.asList("bignore", "");
            }
            mainList.put("ignore", BASE.get(0));
            TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
            aliasList.put("ignore", Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length));
            register(new IgnoreCommand());
            commands++;
        }
        if(enable.contains("join")) {
            BASE = config.getStringList("commands.join");
            if (BASE.isEmpty()) {
                getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
                getLogger().log(Level.WARNING, "Falling back to default value for key commands.join");
                BASE = Arrays.asList("join","");
            }
            mainList.put("join", BASE.get(0));
            TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
            aliasList.put("join", Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length));
            register(new JoinCommand());
            commands++;
        }
        if (enable.contains("list")) {
            BASE = config.getStringList("commands.list");
            if (BASE.isEmpty()) {
                getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
                getLogger().log(Level.WARNING, "Falling back to default value for key commands.list");
                BASE = Arrays.asList("glist","servers","serverlist");
            }
            mainList.put("list", BASE.get(0));
            TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
            aliasList.put("list", Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length));
            register(new ServerListCommand());
            commands++;
        }
        if (enable.contains("message")) {
            BASE = config.getStringList("commands.message");
            if (BASE.isEmpty()) {
                getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
                getLogger().log(Level.WARNING, "Falling back to default value for key commands.message");
                BASE = Arrays.asList("message","msg","m","pm","t","tell","w","whisper");
            }
            mainList.put("message", BASE.get(0));
            TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
            aliasList.put("message", Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length));
            BASE = config.getStringList("commands.reply");
            if (BASE.isEmpty()) {
                getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
                getLogger().log(Level.WARNING, "Falling back to default value for key commands.reply");
                BASE = Arrays.asList("reply","r");
            }
            mainList.put("reply", BASE.get(0));
            TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
            aliasList.put("reply", Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length));
            register(new MessageCommand());
            register(new ReplyCommand());
            commands += 2;
        }
        if (enable.contains("slap")) {
            BASE = config.getStringList("commands.slap");
            if (BASE.isEmpty()) {
                getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
                getLogger().log(Level.WARNING, "Falling back to default value for key commands.slap");
                BASE = Arrays.asList("slap","uslap","smack");
            }
            mainList.put("slap", BASE.get(0));
            TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
            aliasList.put("slap", Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length));
            register(new SlapCommand());
            commands++;
        }
        boolean aliases = enable.contains("aliases");
        if (aliases) {
            AliasManager.load();
            getLogger().log(Level.INFO, "Enabled aliases");
        }
        shouldClean = enable.contains("clean");
        useLog = enable.contains("log");
        logAll = enable.contains("fulllog");
        if (logAll) {
            useLog = true;
        }
        if (useLog) {
            if (!Log.setup()) {
                getLogger().log(Level.WARNING, "Error enabling the chat logger!");
            }
        }
        watchMultiLog = enable.contains("multilog");
        BASE = config.getStringList("commands.reload");
        if (BASE.isEmpty()) {
            getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
            getLogger().log(Level.WARNING, "Falling back to default value for key commands.reload");
            BASE = Arrays.asList("gssreload", "");
        }
        mainList.put("reload", BASE.get(0));
        TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
        aliasList.put("reload", Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length));
        register(new ReloadCommand());
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
        spam = enable.contains("spam");
        if (spam) {
            getLogger().log(Level.INFO, "Enabled spam filter for public chat");
        }
        chatSpam = enable.contains("spam-chat");
        if (chatSpam) {
            getLogger().log(Level.INFO, "Enabled spam filter for private chat");
        }
        if (enable.contains("spy") || enable.contains("hide")) {
            ProxyServer.getInstance().getPluginManager().registerListener(this, new Messenger());
        }

        if (enable.contains("spam") || enable.contains("rules") || enable.contains("multilog") || enable.contains("commandspy") || enable.contains("fulllog")) {
            ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerListener());
        }
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

    public boolean logAll() {
        return this.logAll;
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

    public Configuration getMessages() {
        return this.messages;
    }

    public Configuration getPlayerConfig() {
        return this.players;
    }

    public void saveMainConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(getConfig(), configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMessagesConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(getMessages(), messageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePlayerConfig(String player) {
        try {
            this.players = ConfigurationProvider.getProvider(YamlConfiguration.class).load(playerFile);
            List<String> players = getPlayerConfig().getStringList("players");
            players.add(player);
            getPlayerConfig().set("players", players);
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(getPlayerConfig(), playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IntegrationProvider getIntegrationProvider() {
        return helper;
    }

    public boolean isIntegrated() {
        return integrated && helper != null;
    }

    public boolean useRules() {
        return rules;
    }

    public boolean useSpamProtection() {
        return spam;
    }

    public boolean useChatSpamProtection() {
        return chatSpam;
    }

    public boolean useChatRules() {
        return chatRules;
    }

    public boolean ignore() {
        return ignore;
    }

    public boolean mute() {
        return mute;
    }
}
