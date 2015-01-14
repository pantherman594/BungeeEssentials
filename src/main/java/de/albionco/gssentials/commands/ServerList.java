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

package de.albionco.gssentials.commands;

import de.albionco.gssentials.Dictionary;
import de.albionco.gssentials.Permissions;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Connor Harries on 17/10/2014.
 *
 * @author Connor Spencer Harries
 */
@SuppressWarnings("deprecation")
public class ServerList extends Command {

    public ServerList() {
        super("glist", Permissions.General.LIST, "servers", "serverlist");
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        int online = ProxyServer.getInstance().getPlayers().size();
        sender.sendMessage(Dictionary.format(Dictionary.FORMAT_SERVERS_HEADER, "COUNT", String.valueOf(online)));

        if ((args == null || args.length < 1) || !args[0].equals("-a") && !sender.hasPermission(Permissions.General.LIST_ALL)) {
            displayPingServers(sender, online);
        } else {
            for (ServerInfo info : ProxyServer.getInstance().getServers().values()) {
                int num = info.getPlayers().size();
                sender.sendMessage(Dictionary.format(Dictionary.FORMAT_SERVERS_BODY, "SERVER", info.getName(), "DENSITY", getDensity(num, online), "COUNT", String.valueOf(num)));
            }
        }
    }

    public String getDensity(int serverPlayers, int onlinePlayers) {
        return String.valueOf(getColour(serverPlayers, onlinePlayers)) + "(" + serverPlayers + ")";
    }

    public ChatColor getColour(int serverPlayers, int onlinePlayers) {
        int percent = (int) ((serverPlayers * 100.0f) / onlinePlayers);

        if (percent <= 33) {
            return ChatColor.RED;
        } else if (percent > 33 && percent <= 66) {
            return ChatColor.GOLD;
        } else {
            return ChatColor.GREEN;
        }
    }

    private void displayPingServers(final CommandSender sender, final int onlinePlayers) {
        for (final ServerInfo info : ProxyServer.getInstance().getServers().values()) {
            info.ping(new Callback<ServerPing>() {
                @Override
                public void done(ServerPing serverPing, Throwable throwable) {
                    if (throwable == null) {
                        int num = info.getPlayers().size();
                        sender.sendMessage(Dictionary.format(Dictionary.FORMAT_SERVERS_BODY, "SERVER", info.getName(), "DENSITY", getDensity(num, onlinePlayers), "COUNT", String.valueOf(num)));
                    }
                }
            });
        }
    }
}
