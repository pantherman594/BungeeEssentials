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

import de.albionco.gssentials.Dictionary;
import de.albionco.gssentials.Permissions;
import de.albionco.gssentials.command.ServerSpecificCommand;
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
        super("staff", Permissions.Admin.CHAT, "admin");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args != null && args.length > 0) {
            String server = "CONSOLE";

            if (sender instanceof ProxiedPlayer) {
                server = ((ProxiedPlayer) sender).getServer().getInfo().getName();
            }

            String msg = Dictionary.format(Dictionary.FORMAT_STAFF_CHAT, "SERVER", server, "SENDER", sender.getName(), "MESSAGE", Dictionary.combine(args));

            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (player.hasPermission(Permissions.Admin.CHAT + "." + server) || player.hasPermission(Permissions.Admin.CHAT)) {
                    player.sendMessage(msg);
                }
            }

            CommandSender console = ProxyServer.getInstance().getConsole();
            if (sender == console) {
                console.sendMessage(msg);
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS));
        }
    }
}
