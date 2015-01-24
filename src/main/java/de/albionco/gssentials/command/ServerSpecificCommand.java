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

package de.albionco.gssentials.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Connor Harries on 24/01/2015.
 *
 * @author Connor Spencer Harries
 */
public abstract class ServerSpecificCommand extends Command {
    private String permission;

    public ServerSpecificCommand(String name, String permission, String... aliases) {
        super(name, "", aliases);
        this.permission = permission;
    }

    @Override
    public final void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            String server = player.getServer().getInfo().getName().toLowerCase().replace(" ", "-");
            // Priority goes to server specific permissions
            if (!player.hasPermission(permission + "." + server) || !player.hasPermission(permission)) {
                ProxyServer.getInstance().getTranslation("no_permission");
                return;
            }
        } else {
            if (!sender.hasPermission(permission)) {
                sender.sendMessage(ProxyServer.getInstance().getTranslation("no_permission"));
                return;
            }
        }

        run(sender, args);
    }

    public abstract void run(CommandSender sender, String[] args);
}
