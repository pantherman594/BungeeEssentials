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

package com.pantherman594.gssentials.aliases;

import com.pantherman594.gssentials.BungeeEssentials;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class AliasManager {
    public static Map<String, List<String>> aliases = new HashMap<>();

    private static void register(String alias, List<String> commands) {
        if (!aliases.containsKey(alias)) {
            aliases.put(alias, commands);
            ProxyServer.getInstance().getPluginManager().registerCommand(BungeeEssentials.getInstance(), new LoadCmds(alias, commands));
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean load() {
        aliases.clear();
        Configuration aliasSection = BungeeEssentials.getInstance().getConfig().getSection("aliases");
        for (String alias : aliasSection.getKeys()) {
            List<String> commands = aliasSection.getStringList(alias);
            register(alias, commands);
        }
        if (aliases.size() > 0) {
            BungeeEssentials.getInstance().getLogger().log(Level.INFO, "Loaded {0} aliases from config", aliases.size());
        }
        return true;
    }
}
