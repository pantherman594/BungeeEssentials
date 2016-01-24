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

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class ServerSpecificCommand extends BECommand {
    private final String permission;

    public ServerSpecificCommand(String name, String permission) {
        super(name, "");
        this.permission = permission;
    }

    @SuppressWarnings("deprecation")
	@Override
    public final void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(permission)) {
            run(sender, args);
        } else {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            String server = player.getServer().getInfo().getName().toLowerCase().replace(" ", "-");
            if (player.hasPermission(permission + "." + server)) {
                run(sender, args);
            } else {
                player.sendMessage(ProxyServer.getInstance().getTranslation("no_permission"));
            }
        }
    }

    public abstract void run(CommandSender sender, String[] args);
}
