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

package de.albionco.gssentials.aliases;

import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by David on 5/11/2015.
 *
 * @author David Shen
 */
public class LoadCmds extends Command {
    private final String[] commands;
    private final String main;

    public LoadCmds(String main, String[] commands) {
        super(main);
        this.main = main;
        this.commands = commands;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(Permissions.General.ALIAS) || sender.hasPermission(Permissions.General.ALIAS + "." + main)) {
            for (String command : commands) {
                command = parseCommands(command, sender, args);
                if (command != null) {
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(sender, command);
                } else {
                    sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", "VARIES"));
                }
            }
        } else {
            sender.sendMessage(ProxyServer.getInstance().getTranslation("no_permission"));
        }
    }

    @SuppressWarnings("deprecation")
    private String parseCommands(String command, CommandSender sender, String[] args) {
        int num = 0;
        String server;
        if (sender instanceof ProxiedPlayer) {
            server = ((ProxiedPlayer) sender).getServer().getInfo().getName();
        } else {
            server = "CONSOLE";
        }
        while (args.length > num && command.contains("{" + num + "}")) {
            if ((args[num] != null) && (!args[num].equals(""))) {
                command = command.replace("{" + num + "}", args[num]).replace("{{ PLAYER }}", sender.getName()).replace("{{ SERVER }}", server);
            } else {
                return null;
            }
            num++;
        }
        return command;
    }
}
