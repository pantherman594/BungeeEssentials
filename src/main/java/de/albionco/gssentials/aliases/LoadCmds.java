package de.albionco.gssentials.aliases;

import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

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
