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
import com.pantherman594.gssentials.command.ServerSpecificCommand;
import com.pantherman594.gssentials.event.GlobalChatEvent;
import com.pantherman594.gssentials.utils.Dictionary;
import com.pantherman594.gssentials.utils.Messenger;
import com.pantherman594.gssentials.utils.Permissions;
import com.pantherman594.gssentials.utils.PlayerData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@SuppressWarnings("deprecation")
public class ChatCommand extends ServerSpecificCommand {
    public ChatCommand() {
        super("chat", Permissions.General.CHAT);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args != null && args.length > 0) {
            String server = "CONSOLE";

            if (sender instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) sender;
                PlayerData pD = BungeeEssentials.getInstance().getData(player.getUniqueId());
                server = player.getServer().getInfo().getName();
                if (args.length == 1) {
                    if (args[0].equals("on")) {
                        pD.setGlobalChat(true);
                        player.sendMessage(Dictionary.format(Dictionary.SCHAT_ENABLED));
                        return;
                    } else if (args[0].equals("off")) {
                        pD.setGlobalChat(false);
                        player.sendMessage(Dictionary.format(Dictionary.SCHAT_DISABLED));
                        return;
                    }
                }
            }

            String msg;
            if (sender instanceof ProxiedPlayer) {
                msg = Messenger.filter((ProxiedPlayer) sender, Dictionary.combine(args), Messenger.ChatType.PUBLIC);
            } else {
                msg = Dictionary.combine(args);
            }

            ProxyServer.getInstance().getPluginManager().callEvent(new GlobalChatEvent(server, sender.getName(), msg));

        } else {
            ProxiedPlayer player;
            if (sender instanceof ProxiedPlayer) {
                player = (ProxiedPlayer) sender;
                if (BungeeEssentials.getInstance().getData(player.getUniqueId()).toggleGlobalChat()) {
                    player.sendMessage(Dictionary.format(Dictionary.GCHAT_ENABLED));
                } else {
                    player.sendMessage(Dictionary.format(Dictionary.GCHAT_DISABLED));
                }
            } else {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", getName() + " [on|off|msg]"));
            }
        }
    }
}
