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

package com.pantherman594.gssentials.command.admin;

import com.pantherman594.gssentials.BungeeEssentials;
import com.pantherman594.gssentials.Dictionary;
import com.pantherman594.gssentials.Permissions;
import com.pantherman594.gssentials.command.BECommand;
import net.md_5.bungee.api.CommandSender;

@SuppressWarnings("unused")
public class ReloadCommand extends BECommand {
    public ReloadCommand() {
        super("reload", Permissions.Admin.RELOAD);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (BungeeEssentials.getInstance().reload()) {
            sender.sendMessage(Dictionary.format("&aBungeeEssentials has been reloaded!"));
        } else {
            sender.sendMessage(Dictionary.format("&cUnable to reload BungeeEssentials! :("));
        }
    }
}
