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
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Connor Harries on 17/10/2014.
 *
 * @author Connor Spencer Harries
 */
@SuppressWarnings("deprecation")
public class AlertCommand extends Command {

    public AlertCommand() {
        super("alert", Permissions.Admin.ALERT, "galert");
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
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS));
        }
    }

}
