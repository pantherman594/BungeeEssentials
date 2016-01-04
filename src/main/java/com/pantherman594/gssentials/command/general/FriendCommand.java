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

package com.pantherman594.gssentials.command.general;

import com.pantherman594.gssentials.command.BECommand;
import com.pantherman594.gssentials.utils.Dictionary;
import com.pantherman594.gssentials.utils.Permissions;
import net.md_5.bungee.api.CommandSender;

/**
 * Created by David on 12/05.
 *
 * @author David
 */
public class FriendCommand extends BECommand {

    public FriendCommand() {
        super("friend", Permissions.General.FRIEND);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            //friends list
        } else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            //friends list
        } else if (args.length == 2 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
            //add or remove friend
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", getName() + " [list|add <player>|remove <player>"));
        }
    }
}
