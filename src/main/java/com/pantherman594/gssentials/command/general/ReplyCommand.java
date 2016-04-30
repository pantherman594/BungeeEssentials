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

package com.pantherman594.gssentials.command.general;

import com.pantherman594.gssentials.Dictionary;
import com.pantherman594.gssentials.Messenger;
import com.pantherman594.gssentials.Permissions;
import com.pantherman594.gssentials.command.BECommand;
import com.pantherman594.gssentials.event.MessageEvent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

@SuppressWarnings("WeakerAccess")
public class ReplyCommand extends BECommand {
    public ReplyCommand() {
        super("reply", Permissions.General.MESSAGE);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (args.length > 0) {
                ProxiedPlayer player = (ProxiedPlayer) sender;
                UUID uuid = Messenger.reply(player);
                if (uuid == null) {
                    sender.sendMessage(Dictionary.format(Dictionary.ERROR_NOBODY_HAS_MESSAGED));
                    return;
                }
                ProxiedPlayer recipient = ProxyServer.getInstance().getPlayer(uuid);
                if (recipient == null) {
                    sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_OFFLINE));
                }
                ProxyServer.getInstance().getPluginManager().callEvent(new MessageEvent(sender, recipient, Dictionary.combine(args)));
            } else {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", getName() + " <message>"));
            }
        } else {
            sender.sendMessage(Dictionary.color("&cSorry, only players can reply to messages."));
        }
    }
}
