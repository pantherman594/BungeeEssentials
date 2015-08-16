package de.albionco.gssentials.command.admin;

import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Messenger;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by David on 8/16/2015.
 *
 * @author David
 */
public class MuteCommand extends Command {
    public MuteCommand() {
        super(BungeeEssentials.Mute_MAIN, Permissions.Admin.MUTE, BungeeEssentials.Mute_ALIAS);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args != null && args.length > 0 && sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (Messenger.toggleMute((ProxiedPlayer) sender)) {
                player.sendMessage(Dictionary.format(Dictionary.MUTE_ENABLED));
            } else {
                player.sendMessage(Dictionary.format(Dictionary.MUTE_DISABLED));
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", BungeeEssentials.Mute_MAIN + " <player>"));
        }
    }
}
