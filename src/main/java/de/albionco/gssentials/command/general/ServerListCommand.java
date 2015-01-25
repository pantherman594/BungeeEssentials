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

package de.albionco.gssentials.command.general;

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
public class ServerListCommand extends Command {

    public ServerListCommand() {
        super("glist", Permissions.General.LIST, "servers", "serverlist");
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        int online = ProxyServer.getInstance().getOnlineCount() - Messenger.howManyHidden();
        sender.sendMessage(Dictionary.format(Dictionary.LIST_HEADER, "COUNT", String.valueOf(online)));
        for (final ServerInfo info : ProxyServer.getInstance().getServers().values()) {
            if (sender.hasPermission(Permissions.General.LIST_OFFLINE)) {
                print(sender, info);
            } else {
                info.ping(new Callback<ServerPing>() {
                    @Override
                    public void done(ServerPing serverPing, Throwable throwable) {
                        if (throwable == null) {
                            print(sender, info);
                        }
                    }
                });
            }
        }
    }

    private int getNonHiddenPlayers(ServerInfo info) {
        int result = 0;
        for (ProxiedPlayer player : info.getPlayers()) {
            if (!Messenger.isHidden(player)) {
                result++;
            }
        }
        return result;
    }

    private String getDensity(int players) {
        return String.valueOf(getColour(players)) + "(" + players + ")";
    }

    private ChatColor getColour(int players) {
        if (players == 0 || players < 0) {
            return ChatColor.RED;
        }

        int total = ProxyServer.getInstance().getOnlineCount() - Messenger.howManyHidden();
        double percent = (players * 100.0f) / total;
        if (percent <= 33) {
            return ChatColor.RED;
        } else if (percent > 33 && percent <= 66) {
            return ChatColor.GOLD;
        } else {
            return ChatColor.GREEN;
        }
    }

    private void print(CommandSender sender, ServerInfo info) {
        if (info.canAccess(sender) || sender.hasPermission(Permissions.General.LIST_RESTRICTED)) {
            int online = getNonHiddenPlayers(info);
            sender.sendMessage(Dictionary.format(Dictionary.LIST_BODY, "SERVER", info.getName(), "MOTD", info.getMotd(), "DENSITY", getDensity(online), "COUNT", String.valueOf(online)));
        }
    }
}
