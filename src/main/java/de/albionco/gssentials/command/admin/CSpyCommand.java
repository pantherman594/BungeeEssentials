package de.albionco.gssentials.command.admin;

import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.command.ServerSpecificCommand;
import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Messenger;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@SuppressWarnings("deprecation")
public class CSpyCommand extends ServerSpecificCommand {
    public CSpyCommand() {
        super(BungeeEssentials.CSpy_MAIN, Permissions.Admin.SPY_COMMAND, BungeeEssentials.CSpy_ALIAS);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (args != null && args.length == 1) {
                if (args[0].equals("on")) {
                    Messenger.enableCSpy(player);
                    player.sendMessage(Dictionary.format(Dictionary.CSPY_ENABLED));
                } else if (args[0].equals("off")) {
                    Messenger.disableCSpy(player);
                    player.sendMessage(Dictionary.format(Dictionary.CSPY_DISABLED));
                }
            } else {
                if (Messenger.toggleCSpy(player)) {
                    player.sendMessage(Dictionary.format(Dictionary.CSPY_ENABLED));
                } else {
                    player.sendMessage(Dictionary.format(Dictionary.CSPY_DISABLED));
                }
            }
        } else {
            sender.sendMessage(Dictionary.colour("&cConsole may not toggle command spy"));
        }
    }
}