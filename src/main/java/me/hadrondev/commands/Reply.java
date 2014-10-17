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

package me.hadrondev.commands;

import me.hadrondev.BungeeEssentials;
import me.hadrondev.Chat;
import me.hadrondev.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/**
 * Created by Connor Harries on 17/10/2014.
 */
@SuppressWarnings("deprecation")
public class Reply extends Command {
    public Reply() {
        super("reply", "", "r");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (args.length > 0) {
                ProxiedPlayer player = (ProxiedPlayer) sender;
                UUID uuid = Chat.reply(player);
                ProxiedPlayer recipient = BungeeEssentials.me.getProxy().getPlayer(uuid);
                Chat.sendMessage(player, recipient, Messages.combine(args));
            } else {
                sender.sendMessage(Messages.lazyColour(Messages.INVALID_ARGS));
            }
        } else {
            sender.sendMessage(Messages.lazyColour("&cSorry, only players can reply to messages."));
        }
    }
}
