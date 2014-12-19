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

import com.google.common.collect.ImmutableSet;
import de.albionco.gssentials.Dictionary;
import de.albionco.gssentials.permissions.Permission;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Connor Harries on 17/10/2014.
 *
 * @author Connor Spencer Harries
 */
@SuppressWarnings("deprecation")
public class SendAll extends Command implements TabExecutor {
    public SendAll() {
        super("sendall", Permission.ADMIN_SENDALL);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            ServerInfo info = ProxyServer.getInstance().getServerInfo(args[0]);
            for (final ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                player.connect(info, new Callback<Boolean>() {
                    @Override
                    public void done(Boolean success, Throwable throwable) {
                        if (success) {
                            player.sendMessage(new ComponentBuilder("Whooooooooooosh!").color(ChatColor.LIGHT_PURPLE).create());
                        }
                    }
                });
            }
        } else {
            sender.sendMessage(Dictionary.colour(Dictionary.ERRORS_INVALID));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        if (args.length > 1 || args.length == 0) {
            return ImmutableSet.of();
        }

        Set<String> matches = new HashSet<>();
        String search = args[0].toLowerCase();
        for (String server : ProxyServer.getInstance().getServers().keySet()) {
            if (server.toLowerCase().startsWith(search)) {
                matches.add(server);
            }
        }
        return matches;
    }
}
