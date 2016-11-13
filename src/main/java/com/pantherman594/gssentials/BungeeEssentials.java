/*
 * BungeeEssentials: Full customization of a few necessary features for your server!
 * Copyright (C) 2016 David Shen (PantherMan594)
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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pantherman594.gssentials.aliases.AliasManager;
import com.pantherman594.gssentials.announcement.AnnouncementManager;
import com.pantherman594.gssentials.database.PlayerData;
import com.pantherman594.gssentials.integration.IntegrationProvider;
import com.pantherman594.gssentials.integration.IntegrationTest;
import com.pantherman594.gssentials.regex.RuleManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class BungeeEssentials extends Plugin {
    private static BungeeEssentials instance;
    private Map<String, String> mainList = new HashMap<>();
    private Map<String, String[]> aliasList = new HashMap<>();
    private RuleManager ruleManager;
    private Configuration config;
    private Configuration messages = null;
    private IntegrationProvider helper;
    private List<String> enabled;
    private File libDir;
    private File configFile;
    private File messageFile;
    private Messenger messenger;
    private PlayerData playerData;
    private boolean integrated;

    /**
     * @return The BungeeEssentials instance.
     */
    public static BungeeEssentials getInstance() {
        return instance;
    }

    RuleManager getRuleManager() {
        return ruleManager;
    }

    /**
     * @param key The internal command name.
     * @return The main command.
     */
    public String getMain(String key) {
        return mainList.get(key);
    }

    /**
     * @param key The internal command name.
     * @return A list of the aliases from the main command.
     */
    public String[] getAlias(String key) {
        return aliasList.get(key);
    }

    @Override
    public void onEnable() {
        instance = this;
        libDir = new File(getDataFolder(), "lib");
        configFile = new File(getDataFolder(), "config.yml");
        messageFile = new File(getDataFolder(), "messages.yml");
        try {
            loadConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getConfig().getStringList("enable").contains("updater")) {
            if (new Updater().update(getConfig().getStringList("enable").contains("betaupdates"))) {
                return;
            }
        }
        reload();
        ProxyServer.getInstance().getScheduler().schedule(this, this::reload, 3, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        Log.reset();
    }

    /**
     * Returns the uuid from a player's name.
     *
     * @param name The name of the player.
     * @return The player's uuid if found, null if not.
     */
    public String getOfflineUUID(String name) {
        String uuid;
        String checkUuid = (String) playerData.getData("lastname", name, "uuid");
        if (checkUuid != null) {
            return checkUuid;
        }
        if (ProxyServer.getInstance().getConfig().isOnlineMode()) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openStream()))) {
                uuid = (((JsonObject) new JsonParser().parse(in)).get("id")).toString().replaceAll("\"", "").replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
            } catch (Exception e) {
                return null;
            }
        } else {
            uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8)).toString();
        }

        return uuid;
    }

    /**
     * Tries to save all the config files.
     *
     * @throws IOException The IOException thrown if the files could not be created
     */
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
        file = new File(getDataFolder(), "messages.yml");
        if (!file.exists()) {
            Files.copy(getResourceAsStream("messages.yml"), file.toPath());
        }
    }

    /**
     * Tries to load all the config files.
     *
     * @throws IOException The IOException thrown if the files could not be created
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadConfig() throws IOException {
        if (!libDir.exists()) {
            libDir.mkdir();
        }
        if (!configFile.exists()) {
            saveConfig();
        }
        if (!messageFile.exists()) {
            saveConfig();
        }
        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        messages = ConfigurationProvider.getProvider(YamlConfiguration.class).load(messageFile);
    }

    /**
     * Reload all the config files and re-register all activated commands and listeners.
     *
     * @return Whether reload was successful.
     */
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

        playerData = new PlayerData();
        playerData.createDataNotExist("CONSOLE");
        messenger = new Messenger();
        Log.reset();
        enabled = new ArrayList<>();

        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerListener());

        int commands = 0;
        List<String> BASE;
        String[] TEMP_ALIAS;
        List<String> enable = config.getStringList("enable");
        for (String comm : Arrays.asList("alert", "commandspy", "hide", "lookup", "mute", "sendall", "send", "spy", "staffchat", "chat", "find", "friend", "ignore", "join", "list", "reply", "message", "msggroup", "slap", "reload")) {
            if (enable.contains(comm) || comm.equals("reply") || comm.equals("reload")) {
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
        addEnabled("rules-chat");
        addEnabled("spam-chat");
        addEnabled("spam-command");
        addEnabled("commandSpy");
        addEnabled("fastRelog");
        addEnabled("friend");
        addEnabled("ignore");
        addEnabled("joinAnnounce");
        addEnabled("fulllog");
        addEnabled("hoverlist");
        addEnabled("mute");
        addEnabled("server");
        addEnabled("autoredirect");
        addEnabled("rules");
        addEnabled("spam");
        addEnabled("useLog", "log", "fulllog");
        if (contains("useLog")) {
            if (!Log.setup()) {
                getLogger().log(Level.WARNING, "Error enabling the chat logger!");
            } else {
                getLogger().log(Level.INFO, "Enabled the chat logger");
            }
        }
        if (contains(enable, "aliases")) {
            new AliasManager();
            getLogger().log(Level.INFO, "Enabled aliases");
        }
        if (contains(enable, "announcement")) {
            new AnnouncementManager();
            getLogger().log(Level.INFO, "Enabled announcements");
        }
        if (contains("rules", "rules-chat")) {
            ruleManager = new RuleManager();
        }
        getLogger().log(Level.INFO, "Registered {0} commands successfully", commands);
        setupIntegration();
        return true;
    }

    /**
     * Sets up integration with another plugin.
     *
     * @param ignore Plugins to ignore when setting up integration.
     */
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

    /**
     * Registers the give command.
     *
     * @param comm The internal name of the command to register.
     */
    private void register(String comm) {
        Map<String, String> commands = new HashMap<>();
        commands.put("alert", "com.pantherman594.gssentials.command.admin.AlertCommand");
        commands.put("chat", "com.pantherman594.gssentials.command.general.ChatCommand");
        commands.put("commandspy", "com.pantherman594.gssentials.command.admin.CSpyCommand");
        commands.put("find", "com.pantherman594.gssentials.command.general.FindCommand");
        commands.put("friend", "com.pantherman594.gssentials.command.general.FriendCommand");
        commands.put("hide", "com.pantherman594.gssentials.command.admin.HideCommand");
        commands.put("ignore", "com.pantherman594.gssentials.command.general.IgnoreCommand");
        commands.put("join", "com.pantherman594.gssentials.command.general.JoinCommand");
        commands.put("list", "com.pantherman594.gssentials.command.general.ServerListCommand");
        commands.put("lookup", "com.pantherman594.gssentials.command.admin.LookupCommand");
        commands.put("message", "com.pantherman594.gssentials.command.general.MessageCommand");
        commands.put("msggroup", "com.pantherman594.gssentials.command.general.MsgGroupCommand");
        commands.put("mute", "com.pantherman594.gssentials.command.admin.MuteCommand");
        commands.put("reload", "com.pantherman594.gssentials.command.admin.ReloadCommand");
        commands.put("send", "com.pantherman594.gssentials.command.admin.SendCommand");
        commands.put("slap", "com.pantherman594.gssentials.command.general.SlapCommand");
        commands.put("spy", "com.pantherman594.gssentials.command.admin.SpyCommand");
        commands.put("staffchat", "com.pantherman594.gssentials.command.admin.StaffChatCommand");
        if (commands.containsKey(comm)) {
            try {
                Class cClass = Class.forName(commands.get(comm));
                ProxyServer.getInstance().getPluginManager().registerCommand(this, (Command) cClass.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return The lib directory.
     */
    public File getLibDir() {
        return this.libDir;
    }

    /**
     * @return The main config file.
     */
    public Configuration getConfig() {
        return this.config;
    }

    /**
     * @return The messages config file.
     */
    public Configuration getMessages() {
        return this.messages;
    }

    /**
     * @return The messenger.
     */
    public Messenger getMessenger() {
        return this.messenger;
    }

    /**
     * @return The playerData database.
     */
    public PlayerData getPlayerData() {
        return this.playerData;
    }

    /**
     * Saves the main config.
     */
    void saveMainConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(getConfig(), configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the messages config.
     */
    void saveMessagesConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(getMessages(), messageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return The integration provider.
     */
    public IntegrationProvider getIntegrationProvider() {
        return helper;
    }

    /**
     * Adds modules to the enabled list.
     *
     * @param name Internal name of the module to enable.
     */
    private void addEnabled(String name) {
        addEnabled(name, name);
    }

    /**
     * Enables a module if any of the keys are enabled.
     *
     * @param name Internal name of the module to enable.
     * @param keys All the keys to check in the enabled list.
     */
    private void addEnabled(String name, String... keys) {
        if (contains(config.getStringList("enable"), keys)) enabled.add(name);
    }

    /**
     * Checks whether any of the given strings are found anywhere in the list.
     *
     * @param list   The list to check for matches from.
     * @param checks The strings to check for matches.
     * @return Whether any of the strings are found in the list.
     */
    private boolean contains(List<String> list, String... checks) {
        for (String string : list) {
            for (String check : checks) {
                if (string.equalsIgnoreCase(check)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether any of the given strings are
     * found anywhere in the enabled list.
     *
     * @param checks The strings to check for matches.
     * @return Whether any of the strings are found in the enabled list.
     */
    public boolean contains(String... checks) {
        return contains(enabled, checks);
    }

    /**
     * @return Whether BungeeEssentials is integrated with another plugin.
     */
    public boolean isIntegrated() {
        return integrated && helper != null;
    }
}
