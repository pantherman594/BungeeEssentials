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

import com.google.common.base.Preconditions;

import java.util.Map;

public class Alias {
    private String alias = null;
    private String[] commands = null;

    public static Alias deserialize(Map<String, String> serialized) {
        Preconditions.checkNotNull(serialized);
        Preconditions.checkArgument(!serialized.isEmpty());
        Preconditions.checkNotNull(serialized.get("alias"), "invalid alias");
        Preconditions.checkNotNull(serialized.get("commands"), "invalid commands");

        Alias alias = new Alias();
        alias.alias(String.valueOf(serialized.get("alias")));
        alias.commands(String.valueOf(serialized.get("commands")));
        return alias;
    }

    private Alias alias(String alias) {
        this.alias = alias;
        return this;
    }

    private Alias commands(String commands) {
        commands = commands.replace("[", "").replace("]", "");
        String[] newCmds;
        if (!commands.equals("")) {
            newCmds = commands.split(", ");
            this.commands = newCmds;
        } else {
            this.commands = null;
        }
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public String[] getCommands() {
        return commands;
    }
}
