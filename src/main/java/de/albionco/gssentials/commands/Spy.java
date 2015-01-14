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

import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.Dictionary;
import de.albionco.gssentials.Messenger;
import de.albionco.gssentials.Permissions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.logging.Level;

/**
 * Created by Connor Harries on 14/01/2015.
 *
 * @author Connor Spencer Harries
 */
public class Spy extends Command {

    public Spy() {
        super("spy", Permissions.Admin.SPY, "socialspy", "gspy");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            boolean spy = Messenger.isSpy(player);
            boolean success;
            if (spy) {
                success = Messenger.removeSpy(player);
            } else {
                success = Messenger.addSpy(player);
            }
            if (success) {
                player.sendMessage(Dictionary.format(spy ? Dictionary.FORMAT_SPY_DISABLED : Dictionary.FORMAT_SPY_ENABLED));
            } else {
                BungeeEssentials.getInstance().getLogger().log(Level.INFO, "Unable to toggle social spy for \"{0}\"", player.getName());
            }
        } else {
            sender.sendMessage(new ComponentBuilder("Social spy cannot be used by console").color(ChatColor.RED).create());
        }
    }

}
