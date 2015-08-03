/*
 * Copyright (c) 2015 Connor Spencer Harries
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.albionco.gssentials.command.admin;

import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.command.ServerSpecificCommand;
import de.albionco.gssentials.event.StaffChatEvent;
import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Messenger;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by Connor Harries on 19/12/2014.
 *
 * @author Connor Spencer Harries
 */
@SuppressWarnings("deprecation")
public class ChatCommand extends ServerSpecificCommand {
    public ChatCommand() {
        super(BungeeEssentials.StaffChat_MAIN, Permissions.Admin.CHAT, BungeeEssentials.StaffChat_ALIAS);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args != null && args.length > 0) {
            String server = "CONSOLE";

            if (sender instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) sender;
                server = player.getServer().getInfo().getName();
                if (args.length == 1) {
                    if (args[0].equals("on")) {
                        Messenger.enableStaffChat(player);
                        player.sendMessage(Dictionary.format(Dictionary.SCHAT_ENABLED));
                        return;
                    } else if (args[0].equals("off")) {
                        Messenger.disableStaffChat(player);
                        player.sendMessage(Dictionary.format(Dictionary.SCHAT_DISABLED));
                        return;
                    }
                }
            }

            String msg;
            if (sender instanceof ProxiedPlayer) {
                msg = Messenger.filter((ProxiedPlayer) sender, Dictionary.combine(args));
            } else {
                msg = Dictionary.combine(args);
            }

            ProxyServer.getInstance().getPluginManager().callEvent(new StaffChatEvent(server, sender.getName(), msg));

            CommandSender console = ProxyServer.getInstance().getConsole();
            if (sender == console) {
                console.sendMessage(msg);
            }
        } else {
            ProxiedPlayer player;
            if (sender instanceof ProxiedPlayer) {
                player = (ProxiedPlayer) sender;
                if (Messenger.toggleStaffChat((ProxiedPlayer) sender)) {
                    player.sendMessage(Dictionary.format(Dictionary.SCHAT_ENABLED));
                } else {
                    player.sendMessage(Dictionary.format(Dictionary.SCHAT_DISABLED));
                }
            } else {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS));
            }
        }
    }
}
