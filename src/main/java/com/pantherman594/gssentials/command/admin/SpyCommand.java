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

import com.pantherman594.gssentials.Dictionary;
import com.pantherman594.gssentials.Permissions;
import com.pantherman594.gssentials.PlayerData;
import com.pantherman594.gssentials.command.ServerSpecificCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@SuppressWarnings("unused")
public class SpyCommand extends ServerSpecificCommand {
    public SpyCommand() {
        super("spy", Permissions.Admin.SPY);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        PlayerData pD;
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            pD = PlayerData.getData(player.getUniqueId());
        } else {
            pD = PlayerData.getData("CONSOLE");
        }
        if (args != null && args.length == 1) {
            switch (args[0]) {
                case "on":
                    pD.setSpy(true);
                    sender.sendMessage(Dictionary.format(Dictionary.SPY_ENABLED));
                    break;
                case "off":
                    pD.setSpy(false);
                    sender.sendMessage(Dictionary.format(Dictionary.SPY_DISABLED));
                    break;
                default:
                    sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", getName() + " [on|off]"));
                    break;
            }
        } else {
            if (pD.toggleSpy()) {
                sender.sendMessage(Dictionary.format(Dictionary.SPY_ENABLED));
            } else {
                sender.sendMessage(Dictionary.format(Dictionary.SPY_DISABLED));
            }
        }
    }
}
