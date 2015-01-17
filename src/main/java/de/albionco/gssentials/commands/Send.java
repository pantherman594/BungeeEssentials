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

import com.google.common.collect.ImmutableSet;
import de.albionco.gssentials.Dictionary;
import de.albionco.gssentials.Messenger;
import de.albionco.gssentials.Permissions;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public class Send extends Command implements TabExecutor {
    public Send() {
        super("send", Permissions.Admin.SEND);
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        if (args.length > 1) {
            final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
            if (player != null) {
                sender.sendMessage(Dictionary.format(Dictionary.FORMAT_SEND, "PLAYER", args[0], "SERVER", args[1]));

                player.connect(ProxyServer.getInstance().getServerInfo(args[1]),
                        new Callback<Boolean>() {
                            @Override
                            public void done(Boolean success, Throwable throwable) {
                                if (success) {
                                    player.sendMessage(Dictionary.colour("&dWhooooooooooosh!"));
                                } else {
                                    // Pretend nothing happened for the player being sent
                                    sender.sendMessage(ChatColor.RED + "Unable to send player to server.");
                                }
                            }
                        });
            } else {
                sender.sendMessage(Dictionary.format(Dictionary.ERRORS_OFFLINE));
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERRORS_INVALID));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length > 2 || args.length == 0) {
            return ImmutableSet.of();
        }

        Set<String> matches = new HashSet<>();
        String search;
        if (args.length == 1) {
            search = args[0].toLowerCase();
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (!player.getName().equals(sender.getName())) {
                    if (player.getName().toLowerCase().startsWith(search) && !Messenger.isHidden(player)) {
                        matches.add(player.getName());
                    }
                }
            }
        } else {
            search = args[1].toLowerCase();
            for (String server : ProxyServer.getInstance().getServers().keySet()) {
                if (server.toLowerCase().startsWith(search)) {
                    matches.add(server);
                }
            }
        }
        return matches;
    }
}
