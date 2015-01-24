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

import com.google.common.collect.ImmutableSet;
import de.albionco.gssentials.Dictionary;
import de.albionco.gssentials.Messenger;
import de.albionco.gssentials.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
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
public class FindCommand extends Command implements TabExecutor {
    public FindCommand() {
        super("find", Permissions.General.FIND);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);

            if (player != null && Messenger.isHidden(player)) {
                sender.sendMessage(Dictionary.format(Dictionary.FORMAT_FIND, "SERVER", player.getServer().getInfo().getName(), "PLAYER", player.getName()));
            } else {
                sender.sendMessage(Dictionary.format(Dictionary.ERRORS_OFFLINE));
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERRORS_INVALID));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length > 1 || args.length == 0) {
            return ImmutableSet.of();
        }

        ProxiedPlayer senderPlayer = null;
        if (sender instanceof ProxiedPlayer) {
            senderPlayer = (ProxiedPlayer) sender;
        }
        Set<String> matches = new HashSet<>();
        String search = args[0].toLowerCase();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (senderPlayer != null) {
                if (player.getServer().getInfo().getName().equals(senderPlayer.getServer().getInfo().getName())) {
                    continue;
                }
            }
            if (!player.getName().equals(sender.getName())) {
                if (player.getName().toLowerCase().startsWith(search) && !Messenger.isHidden(player)) {
                    matches.add(player.getName());
                }
            }
        }
        return matches;
    }
}
