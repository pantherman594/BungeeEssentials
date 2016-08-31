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

package com.pantherman594.gssentials.command;

import com.pantherman594.gssentials.BungeeEssentials;
import com.pantherman594.gssentials.Messenger;
import com.pantherman594.gssentials.Permissions;
import com.pantherman594.gssentials.database.PlayerData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by David on 9/2/2015.
 *
 * @author David
 */
public abstract class BECommand extends Command {
    public PlayerData pD = BungeeEssentials.getInstance().getPlayerData();

    public BECommand(String name, String permission) {
        super(BungeeEssentials.getInstance().getMain(name), permission, BungeeEssentials.getInstance().getAlias(name));
    }

    /**
     * Tab-completion for player lists.
     *
     * @param sender The command sender (to check whether can see hidden players).
     * @param search The search string.
     * @return A list of visible players on the proxy that match the search string.
     */
    public Iterable<String> tabPlayers(CommandSender sender, String search) {
        return Messenger.getVisiblePlayers(sender.hasPermission(Permissions.Admin.SEE_HIDDEN)).stream().filter(player -> !player.getName().equals(sender.getName()) && player.getName().toLowerCase().startsWith(search.toLowerCase())).map(ProxiedPlayer::getName).collect(Collectors.toSet());
    }

    /**
     * Tab-completion for given strings.
     *
     * @param search  The search string.
     * @param strings The given strings to search from (usually commands to tab-complete).
     * @return A list of strings from the list that match the search string.
     */
    protected Iterable<String> tabStrings(String search, String[] strings) {
        Set<String> matches = new HashSet<>();
        for (String string : strings) {
            if (string.toLowerCase().startsWith(search.toLowerCase())) {
                matches.add(string);
            }
        }
        return matches;
    }
}
