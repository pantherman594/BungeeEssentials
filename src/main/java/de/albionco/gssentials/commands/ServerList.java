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
import de.albionco.gssentials.Messenger;
import de.albionco.gssentials.Permissions;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
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
        int online = 0;
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (!Messenger.isHidden(player)) {
                online++;
            }
        }

        sender.sendMessage(Dictionary.format(Dictionary.LIST_HEADER, "COUNT", String.valueOf(online)));

        if (sender.hasPermission(Permissions.General.LIST_OFFLINE)) {
            for (ServerInfo info : ProxyServer.getInstance().getServers().values()) {
                if (info.canAccess(sender) || sender.hasPermission(Permissions.General.LIST_RESTRICTED)) {
                    send(online, sender, info);
                }
            }
        } else {
            displayPingServers(sender, online);
        }
    }

    public int getNonHiddenPlayers(ServerInfo info) {
        int result = 0;
        for (ProxiedPlayer player : info.getPlayers()) {
            if (!Messenger.isHidden(player)) {
                result++;
            }
        }
        return result;
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

    private void send(int onlinePlayers, CommandSender sender, ServerInfo info) {
        int num = getNonHiddenPlayers(info);
        sender.sendMessage(Dictionary.format(Dictionary.LIST_BODY, "SERVER", info.getName(), "DENSITY", getDensity(num, onlinePlayers), "COUNT", String.valueOf(num)));
    }

    private void displayPingServers(final CommandSender sender, final int onlinePlayers) {
        for (final ServerInfo info : ProxyServer.getInstance().getServers().values()) {
            if (info.canAccess(sender) || sender.hasPermission(Permissions.General.LIST_RESTRICTED)) {
                info.ping(new Callback<ServerPing>() {
                    @Override
                    public void done(ServerPing serverPing, Throwable throwable) {
                        if (throwable == null) {
                            send(onlinePlayers, sender, info);
                        }
                    }
                });
            }
        }
    }
}
