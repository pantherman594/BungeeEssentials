package de.albionco.gssentials.command.admin;

import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.command.ServerSpecificCommand;
import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Messenger;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@SuppressWarnings("deprecation")
public class SpyCommand extends ServerSpecificCommand {
    public SpyCommand() {
        super(BungeeEssentials.Spy_MAIN, Permissions.Admin.SPY, BungeeEssentials.Spy_ALIAS);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (args != null && args.length == 1) {
                if (args[0].equals("on")) {
                    Messenger.enableSpy(player);
                    player.sendMessage(Dictionary.format(Dictionary.SPY_ENABLED));
                } else if (args[0].equals("off")) {
                    Messenger.disableSpy(player);
                    player.sendMessage(Dictionary.format(Dictionary.SPY_DISABLED));
                }
            } else {
                if (Messenger.toggleSpy(player)) {
                    player.sendMessage(Dictionary.format(Dictionary.SPY_ENABLED));
                } else {
                    player.sendMessage(Dictionary.format(Dictionary.SPY_DISABLED));
                }
            }
        } else {
            sender.sendMessage(Dictionary.colour("&cConsole may not toggle social spy"));
        }
    }
}
