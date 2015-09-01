package de.albionco.gssentials.command.admin;

import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.command.ServerSpecificCommand;
import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Messenger;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@SuppressWarnings("deprecation")
public class HideCommand extends ServerSpecificCommand {
    public HideCommand() {
        super(BungeeEssentials.Hide_MAIN, Permissions.Admin.HIDE, BungeeEssentials.Hide_ALIAS);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (args != null && args.length == 1) {
                if (args[0].equals("on")) {
                    Messenger.enableHidden(player);
                    player.sendMessage(Dictionary.format(Dictionary.HIDE_ENABLED));
                } else if (args[0].equals("off")) {
                    Messenger.disableHidden(player);
                    player.sendMessage(Dictionary.format(Dictionary.HIDE_DISABLED));
                }
            } else {
                if (Messenger.toggleHidden(player)) {
                    player.sendMessage(Dictionary.format(Dictionary.HIDE_ENABLED));
                } else {
                    player.sendMessage(Dictionary.format(Dictionary.HIDE_DISABLED));
                }
            }
        } else {
            sender.sendMessage(Dictionary.colour("&cConsole cannot hide itself"));
        }
    }
}
