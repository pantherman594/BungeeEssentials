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
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David on 9/2/2015.
 *
 * @author David
 */
public abstract class BECommand extends Command {
    public BECommand(String name, String permission) {
        super(BungeeEssentials.getInstance().getMain(name), permission, BungeeEssentials.getInstance().getAlias(name));
    }

    public Iterable<String> tabPlayers(CommandSender sender, String search) {
        Set<String> matches = new HashSet<>();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (!player.getName().equals(sender.getName())) {
                if (player.getName().toLowerCase().startsWith(search.toLowerCase())) {
                    matches.add(player.getName());
                }
            }
        }
        return matches;
    }

    public Iterable<String> tabStrings(CommandSender sender, String search, String[] strings) {
        Set<String> matches = new HashSet<>();
        for (String string : strings) {
            if (string.toLowerCase().startsWith(search.toLowerCase())) {
                matches.add(string);
            }
        }
        return matches;
    }
}
