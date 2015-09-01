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

import com.pantherman594.gssentials.BungeeEssentials;
import com.pantherman594.gssentials.event.MessageEvent;
import com.pantherman594.gssentials.utils.Dictionary;
import com.pantherman594.gssentials.utils.Messenger;
import com.pantherman594.gssentials.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class ReplyCommand extends Command {
    public ReplyCommand() {
        super(BungeeEssentials.Reply_MAIN, Permissions.General.MESSAGE, BungeeEssentials.Reply_ALIAS);
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
                ProxyServer.getInstance().getPluginManager().callEvent(new MessageEvent(sender, recipient, Dictionary.combine(args)));
            } else {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", BungeeEssentials.Reply_MAIN + " <message>"));
            }
        } else {
            sender.sendMessage(Dictionary.colour("&cSorry, only players can reply to messages."));
        }
    }
}
