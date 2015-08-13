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

import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David on 5/14/2015.
 *
 * @author David Shen
 */
@SuppressWarnings("deprecation")
public class LookupCommand extends Command {
    public LookupCommand() {
        super(BungeeEssentials.Lookup_MAIN, Permissions.Admin.LOOKUP, BungeeEssentials.Lookup_ALIAS);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Set<String> matches = new HashSet<>();
        if (args.length > 0) {
            String partialPlayerName = args[0].toLowerCase();
            for (String p : BungeeEssentials.getInstance().getPlayerConfig().getStringList("players")) {
                if (p.toLowerCase().startsWith(partialPlayerName.toLowerCase())) {
                    matches.add(p);
                }
            }
            sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_HEADER, "SIZE", String.valueOf(matches.size())));
            for (String match : matches) {
                sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_BODY, "PLAYER", match));
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS));
        }
    }
}
