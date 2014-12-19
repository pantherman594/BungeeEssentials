/*
 * Copyright (c) 2014 Connor Spencer Harries
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
import de.albionco.gssentials.permissions.Permission;
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
        super("glist", Permission.LIST, "servers", "serverlist");
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        sender.sendMessage(Dictionary.colour(Dictionary.SETTINGS_GLIST_HEADER));

        if (args != null) {
            if (args.length > 0) {
                if (args[0].equals("-a") && sender.hasPermission(Permission.LIST_ALL)) {
                    sender.sendMessage(Dictionary.colour("&bShowing &aall &bservers"));
                    for (ServerInfo info : ProxyServer.getInstance().getServers().values()) {
                        int num = info.getPlayers().size();
                        sender.sendMessage(Dictionary.format(Dictionary.SETTINGS_GLIST_SERVER, "SERVER", info.getName(), "DENSITY", getDensity(num), "COUNT", String.valueOf(num)));
                    }
                } else {
                    displayPingServers(sender);
                }
            } else {
                displayPingServers(sender);
            }
        } else {
            displayPingServers(sender);
        }
    }

    public String getDensity(int players) {
        return String.valueOf(getColour(players)) + " (" + players + ") ";
    }

    public ChatColor getColour(int players) {
        int online = ProxyServer.getInstance().getOnlineCount();
        int percent = (int) ((players * 100.0f) / online);

        if (percent < 33) {
            return ChatColor.RED;
        } else if (percent > 33 && percent < 66) {
            return ChatColor.GOLD;
        } else {
            return ChatColor.GREEN;
        }
    }

    private void displayPingServers(final CommandSender sender) {
        for (final ServerInfo info : ProxyServer.getInstance().getServers().values()) {
            info.ping(new Callback<ServerPing>() {
                @Override
                public void done(ServerPing serverPing, Throwable throwable) {
                    if (throwable == null) {
                        String message = Dictionary.SETTINGS_GLIST_SERVER;
                        message = message.replace("{SERVER}", info.getName());
                        message = message.replace("{DENSITY}", getDensity(info.getPlayers().size()));
                        message = message.replace("{COUNT}", "" + info.getPlayers().size());

                        sender.sendMessage(Dictionary.colour(message));
                    }
                }
            });
        }
        sender.sendMessage(Dictionary.colour("&bOnly showing &aonline &bservers"));
    }
}
