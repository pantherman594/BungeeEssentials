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
import com.pantherman594.gssentials.command.BECommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@SuppressWarnings("unused")
public class AlertCommand extends BECommand {
    public AlertCommand() {
        super("alert", Permissions.Admin.ALERT);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            String server = "";
            if (sender instanceof ProxiedPlayer) {
                server = ((ProxiedPlayer) sender).getServer().getInfo().getName();
            }
            ProxyServer.getInstance().broadcast(Dictionary.format(Dictionary.FORMAT_ALERT, "SENDER", sender.getName(), "SERVER", server, "MESSAGE", Dictionary.combine(args)));
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", getName() + " <msg>"));
        }
    }

}
