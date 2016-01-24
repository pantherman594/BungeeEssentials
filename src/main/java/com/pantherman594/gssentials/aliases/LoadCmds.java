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

package com.pantherman594.gssentials.aliases;

import com.pantherman594.gssentials.Dictionary;
import com.pantherman594.gssentials.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;

public class LoadCmds extends Command {
    private final List<String> commands;
    private final String main;

    public LoadCmds(String main, List<String> commands) {
        super(main);
        this.main = main;
        this.commands = commands;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(Permissions.General.ALIAS) || sender.hasPermission(Permissions.General.ALIAS + "." + main)) {
            for (String command : commands) {
                runCommand(command, sender, args);
            }
        } else {
            sender.sendMessage(ProxyServer.getInstance().getTranslation("no_permission"));
        }
    }

    private void runCommand(String command, CommandSender sender, String[] args) {
        int num = 0;
        String server;
        if (sender instanceof ProxiedPlayer) {
            server = ((ProxiedPlayer) sender).getServer().getInfo().getName();
        } else {
            server = "CONSOLE";
        }
        while (args.length > num && command.contains("{" + num + "}")) {
            if ((args[num] != null) && (!args[num].equals(""))) {
                command = command.replace("{" + num + "}", args[num]);
            } else {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", "VARIES"));
                return;
            }
            num++;
        }
        command = command.replace("{{ PLAYER }}", sender.getName()).replace("{{ SERVER }}", server);
        ProxyServer.getInstance().getLogger().info(command);
        switch (command.contains(" ") ? command.split(" ")[0] : command) {
            case "CONSOLE:":
                ProxyServer.getInstance().getLogger().info(command.substring(9));
                ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), command.substring(9));
                break;
            case "TELL":
                String commToSplit = command.replaceFirst(": ", "Ƃ");
                String message = commToSplit.split("Ƃ")[1];
                String recipient = commToSplit.split("Ƃ")[0].substring(5);
                if (recipient.equals("ALL")) {
                    for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                        p.sendMessage(Dictionary.colour(message));
                    }
                } else if (command != null && ProxyServer.getInstance().getPlayer(recipient) != null) {
                    ProxyServer.getInstance().getPlayer(recipient).sendMessage(Dictionary.colour(message));
                }
                break;
            case "TELL:":
                sender.sendMessage(Dictionary.colour(command.substring(6)));
                break;
            default:
                ProxyServer.getInstance().getPluginManager().dispatchCommand(sender, command);
        }
    }
}
