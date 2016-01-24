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

package com.pantherman594.gssentials.command.admin;

import com.pantherman594.gssentials.Dictionary;
import com.pantherman594.gssentials.Permissions;
import com.pantherman594.gssentials.PlayerData;
import com.pantherman594.gssentials.command.ServerSpecificCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@SuppressWarnings("deprecation")
public class SpyCommand extends ServerSpecificCommand {
    public SpyCommand() {
        super("spy", Permissions.Admin.SPY);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            PlayerData pD = PlayerData.getData(player.getUniqueId());
            if (args != null && args.length == 1) {
                if (args[0].equals("on")) {
                    pD.setSpy(true);
                    player.sendMessage(Dictionary.format(Dictionary.SPY_ENABLED));
                } else if (args[0].equals("off")) {
                    pD.setSpy(false);
                    player.sendMessage(Dictionary.format(Dictionary.SPY_DISABLED));
                } else {
                    sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", getName() + " [on|off]"));
                }
            } else {
                if (pD.toggleSpy()) {
                    player.sendMessage(Dictionary.format(Dictionary.SPY_ENABLED));
                } else {
                    player.sendMessage(Dictionary.format(Dictionary.SPY_DISABLED));
                }
            }
        } else {
            sender.sendMessage(Dictionary.colour("&cConsole may not toggle social spy"));
        }
    }
}
