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
import com.pantherman594.gssentials.aliases.AliasManager;
import com.pantherman594.gssentials.announcement.AnnouncementManager;
import com.pantherman594.gssentials.integration.IntegrationProvider;
import com.pantherman594.gssentials.integration.IntegrationTest;
import com.pantherman594.gssentials.regex.RuleManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
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
    private static BungeeEssentials instance;
    public List<String> playerList = new ArrayList<>();
    private Map<String, String> mainList = new HashMap<>();
    private Map<String, String[]> aliasList = new HashMap<>();
    private RuleManager ruleManager;
    private Configuration config;
    private Configuration messages = null;
    private Configuration players = null;
    private IntegrationProvider helper;
    private List<String> enabled;
    private File configFile;
    private File messageFile;
    private File playerFile;
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
        configFile = new File(getDataFolder(), "config.yml");
        messageFile = new File(getDataFolder(), "messages.yml");
        playerFile = new File(getDataFolder(), "players.yml");
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
        ProxyServer.getInstance().getScheduler().schedule(this, this::reload, 3, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        PlayerData.getData("CONSOLE").save();
        Log.reset();
        savePlayerConfig();
    }

    /**
     * Tries to save all the config files.
     *
     * @throws IOException
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
        file = new File(getDataFolder(), "players.yml");
        if (!file.exists()) {
            Files.copy(getResourceAsStream("players.yml"), file.toPath());
        }
        file = new File(getDataFolder(), "messages.yml");
        if (!file.exists()) {
            Files.copy(getResourceAsStream("messages.yml"), file.toPath());
        }
    }

    /**
     * Tries to load all the config files.
     *
     * @throws IOException
     */
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
        ProxyServer.getInstance().getPluginManager().registerListener(this, new Messenger());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerListener());

        PlayerData.clearData();
        Log.reset();
        enabled = new ArrayList<>();

        int commands = 0;
        List<String> BASE;
        String[] TEMP_ALIAS;
        List<String> enable = config.getStringList("enable");
        for (String comm : Arrays.asList("alert", "commandspy", "hide", "lookup", "mute", "sendall", "send", "spy", "staffchat", "chat", "find", "friend", "ignore", "join", "list", "reply", "message", "slap", "reload")) {
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
        addEnabled("mute");
        addEnabled("server");
        addEnabled("autoredirect");
        addEnabled("rules");
        addEnabled("clean");
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
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            new PlayerData(p.getUniqueId().toString(), p.getName());
        }
        if (!PlayerData.getDatas().containsKey("CONSOLE")) {
            new PlayerData("CONSOLE", "Console");
        }
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
     * @return The player list file.
     */
    private Configuration getPlayerConfig() {
        return this.players;
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
     * Adds a player to the list of players.
     *
     * @param player The name of the player to add.
     */
    void savePlayerConfig(String player) {
        playerList.add(player);
    }

    /**
     * Saves the player list to a file and reloads it.
     */
    private void savePlayerConfig() {
        try {
            this.players = ConfigurationProvider.getProvider(YamlConfiguration.class).load(playerFile);
            if (!playerList.isEmpty()) {
                getPlayerConfig().set("players", playerList);
            }
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(getPlayerConfig(), playerFile);
            playerList = new ArrayList<>();
            getPlayerConfig().getStringList("players").stream().filter(player -> !playerList.contains(player)).forEachOrdered(player -> playerList.add(player));
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
