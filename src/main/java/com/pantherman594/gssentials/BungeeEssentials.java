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
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class BungeeEssentials extends Plugin {
    public static HashMap<String, String> mainList = new HashMap<>();
    public static HashMap<String, String[]> aliasList = new HashMap<>();
    private static BungeeEssentials instance;
    public List<String> playerList = new ArrayList<>();
    private Configuration config = null;
    private Configuration messages = null;
    private Configuration players = null;
    private IntegrationProvider helper;
    private boolean shouldClean;
    private boolean joinAnnounce;
    private boolean commandSpy;
    private boolean fastRelog;
    private boolean redirect;
    private boolean integrated;
    private boolean chatRules;
    private boolean chatSpam;
    private boolean commandSpam;
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
        savePlayerConfig();
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
        savePlayerConfig();
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

        ProxyServer.getInstance().getScheduler().cancel(this);
        ProxyServer.getInstance().getPluginManager().unregisterCommands(this);
        ProxyServer.getInstance().getPluginManager().unregisterListeners(this);
        ProxyServer.getInstance().getPluginManager().registerListener(this, new Messenger());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerListener());

        Log.reset();
        chatRules = false;
        chatSpam = false;
        rules = false;
        spam = false;
        redirect = false;
        fastRelog = false;
        commandSpam = false;

        int commands = 0;
        List<String> BASE;
        String[] TEMP_ALIAS;
        List<String> enable = config.getStringList("enable");
        for (String comm : Arrays.asList("alert", "commandspy", "hide", "lookup", "mute", "send", "spy", "staffchat", "chat", "find", "friend", "ignore", "join", "list", "message", "slap", "reload")) {
            if (enable.contains(comm)) {
                BASE = config.getStringList("commands." + comm);
                if (BASE.isEmpty()) {
                    getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
                    getLogger().log(Level.WARNING, "Falling back to main value for key commands." + comm);
                    BASE = Collections.singletonList(comm);
                }
                mainList.put(comm, BASE.get(0));
                TEMP_ALIAS = BASE.toArray(new String[BASE.size()]);
                aliasList.put(comm, Arrays.copyOfRange(TEMP_ALIAS, 1, TEMP_ALIAS.length));
                register(comm);
                commands++;
            }
        }
        redirect = enable.contains("autoredirect");
        fastRelog = enable.contains("fastrelog");
        commandSpy = enable.contains("commandspy");
        joinAnnounce = enable.contains("joinannounce");
        mute = enable.contains("mute");
        ignore = enable.contains("ignore");
        shouldClean = enable.contains("clean");
        useLog = enable.contains("log");
        logAll = enable.contains("fulllog");
        rules = enable.contains("rules");
        chatRules = enable.contains("rules-chat");
        if (logAll) {
            useLog = true;
        }
        if (useLog) {
            if (!Log.setup()) {
                getLogger().log(Level.WARNING, "Error enabling the chat logger!");
            }
        }
        if (enable.contains("announcement")) {
            AnnouncementManager.load();
            getLogger().log(Level.INFO, "Enabled announcements");
        }
        if (enable.contains("aliases")) {
            AliasManager.load();
            getLogger().log(Level.INFO, "Enabled aliases");
        }
        if (rules || chatRules) {
            RuleManager.load();
        }
        spam = enable.contains("spam");
        chatSpam = enable.contains("spam-chat");
        commandSpam = enable.contains("spam-commmand");
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

    private void register(String command) {
        switch (command) {
            case "alert":
                register(new AlertCommand());
                break;
            case "chat":
                register(new ChatCommand());
                break;
            case "commandspy":
                register(new CSpyCommand());
                break;
            case "find":
                register(new FindCommand());
                break;
            case "friend":
                register(new FriendCommand());
                break;
            case "hide":
                register(new HideCommand());
                break;
            case "ignore":
                register(new FindCommand());
                break;
            case "join":
                register(new JoinCommand());
                break;
            case "list":
                register(new ServerListCommand());
                break;
            case "lookup":
                register(new LookupCommand());
                break;
            case "message":
                register(new MessageCommand());
                register(new ReplyCommand());
                break;
            case "mute":
                register(new MuteCommand());
                break;
            case "send":
                register(new SendCommand());
                register(new SendAllCommand());
                break;
            case "slap":
                register(new SlapCommand());
                break;
            case "spy":
                register(new SpyCommand());
                break;
            case "staffchat":
                register(new StaffChatCommand());
                break;
        }
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

    public boolean shouldClean() {
        return this.shouldClean;
    }

    public boolean shouldAnnounce() {
        return this.joinAnnounce;
    }

    public boolean shouldCommandSpy() {
        return this.commandSpy;
    }

    public boolean shouldRedirectPlayers() {
        return this.redirect;
    }

    public boolean watchFastRelog() {
        return this.fastRelog;
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
        playerList.add(player);
    }

    public void savePlayerConfig() {
        try {
            this.players = ConfigurationProvider.getProvider(YamlConfiguration.class).load(playerFile);
            if (!playerList.isEmpty()) {
                getPlayerConfig().set("players", playerList);
            }
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(getPlayerConfig(), playerFile);
            playerList = getPlayerConfig().getStringList("players");
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

    public boolean useCommandSpamProtection() {
        return commandSpam;
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
