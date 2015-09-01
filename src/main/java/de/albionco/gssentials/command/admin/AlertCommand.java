package de.albionco.gssentials.command.admin;

import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

@SuppressWarnings("deprecation")
public class AlertCommand extends Command {

    public AlertCommand() {
        super(BungeeEssentials.Alert_MAIN, Permissions.Admin.ALERT, BungeeEssentials.Alert_ALIAS);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            String server = "";
            if (sender instanceof ProxiedPlayer) {
                server = ((ProxiedPlayer) sender).getServer().getInfo().getName();
            }
            ProxyServer.getInstance().broadcast(Dictionary.format(Dictionary.FORMAT_ALERT, "SENDER", sender.getName(), "SERVER", server, "MESSAGE", Dictionary.combine(args)));
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", BungeeEssentials.Alert_MAIN + " <message>"));
        }
    }

}
