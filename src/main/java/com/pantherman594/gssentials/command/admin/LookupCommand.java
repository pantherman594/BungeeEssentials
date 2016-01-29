/*
 * BungeeEssentials: Full customization of a few necessary features for your server!
 * Copyright (C) 2016 David Shen (PantherMan594)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pantherman594.gssentials.command.admin;

import com.pantherman594.gssentials.BungeeEssentials;
import com.pantherman594.gssentials.Dictionary;
import com.pantherman594.gssentials.Permissions;
import com.pantherman594.gssentials.command.ServerSpecificCommand;
import net.md_5.bungee.api.CommandSender;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public class LookupCommand extends ServerSpecificCommand {
    public LookupCommand() {
        super("lookup", Permissions.Admin.LOOKUP);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Set<String> matches = new HashSet<>();
        if (args.length == 1) {
            String partialPlayerName = args[0].toLowerCase();
            for (String p : BungeeEssentials.getInstance().playerList) {
                if (p.toLowerCase().contains(partialPlayerName.toLowerCase())) {
                    matches.add(p);
                }
            }
            sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_HEADER, "SIZE", String.valueOf(matches.size())));
            for (String match : matches) {
                sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_BODY, "PLAYER", match));
            }
        } else if (args.length == 2) {
            boolean error = true;
            String partialPlayerName = args[0].toLowerCase();
            int arg = 0;
            String[] possibleArgs = new String[]{"b", "m", "e", "a"};
            for (String a : possibleArgs) {
                if (args[0].equals("-" + a)) {
                    partialPlayerName = args[1].toLowerCase();
                    error = false;
                } else if (args[1].equals("-" + a)) {
                    arg = 1;
                    error = false;
                }
            }
            if (error) {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", getName() + " <part of name> [-b|-m|-e|-a]"));
            } else {
                switch (args[arg]) {
                    case "-m":
                        for (String p : BungeeEssentials.getInstance().playerList) {
                            if (p.toLowerCase().substring(1, p.length() - 1).contains(partialPlayerName.toLowerCase())) {
                                matches.add(p);
                            }
                        }
                        break;
                    case "-e":
                        for (String p : BungeeEssentials.getInstance().playerList) {
                            if (p.toLowerCase().endsWith(partialPlayerName.toLowerCase())) {
                                matches.add(p);
                            }
                        }
                        break;
                    case "-b":
                        for (String p : BungeeEssentials.getInstance().playerList) {
                            if (p.toLowerCase().startsWith(partialPlayerName.toLowerCase())) {
                                matches.add(p);
                            }
                        }
                        break;
                    case "-a":
                        for (String p : BungeeEssentials.getInstance().playerList) {
                            if (p.toLowerCase().contains(partialPlayerName.toLowerCase())) {
                                matches.add(p);
                            }
                        }
                        break;
                    default:
                        break;
                }
                sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_HEADER, "SIZE", String.valueOf(matches.size())));
                for (String match : matches) {
                    sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_BODY, "PLAYER", match));
                }
            }
        } else if (args.length == 1) {
            String partialPlayerName = args[0].toLowerCase();
            for (String p : BungeeEssentials.getInstance().playerList) {
                if (p.toLowerCase().startsWith(partialPlayerName.toLowerCase())) {
                    matches.add(p);
                }
            }
            sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_HEADER, "SIZE", String.valueOf(matches.size())));
            for (String match : matches) {
                sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_BODY, "PLAYER", match));
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", getName() + " <part of name> [-b|-m|-e|-a]"));
        }
    }
}
