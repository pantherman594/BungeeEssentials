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

import com.google.common.collect.ImmutableSet;
import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Connor Harries on 17/10/2014.
 *
 * @author Connor Spencer Harries
 */
@SuppressWarnings("deprecation")
public class SendAllCommand extends Command implements TabExecutor {
    public SendAllCommand() {
        super(BungeeEssentials.SendAll_MAIN, Permissions.Admin.SENDALL, BungeeEssentials.SendAll_ALIAS);
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        ServerInfo sInfo = ProxyServer.getInstance().getServerInfo(args[0]);
        Collection<ProxiedPlayer> players = ProxyServer.getInstance().getPlayers();
        if (args.length > 1) {
            sInfo = ProxyServer.getInstance().getServerInfo(args[1]);
            players = ProxyServer.getInstance().getServerInfo(args[0]).getPlayers();
        }
        final ServerInfo info = sInfo;
        if (args.length > 0) {
            for (final ProxiedPlayer player : players) {
                player.connect(info, new Callback<Boolean>() {
                    @Override
                    public void done(Boolean success, Throwable throwable) {
                        if (!success) {
                            sender.sendMessage(Dictionary.format(Dictionary.ERROR_SENDFAIL, "PLAYER", player.getName(), "SERVER", info.getName()));
                        }
                    }
                });
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", BungeeEssentials.SendAll_MAIN + " [from server] <to server>"));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        if (args.length == 1 || args.length == 2) {
            Set<String> matches = new HashSet<>();
            String search = args[args.length - 1].toLowerCase();
            for (String server : ProxyServer.getInstance().getServers().keySet()) {
                if (server.toLowerCase().startsWith(search)) {
                    matches.add(server);
                }
            }
            return matches;
        }
        return ImmutableSet.of();
    }
}
