package de.albionco.gssentials.command.admin;

import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Messenger;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MuteCommand extends Command {
    public MuteCommand() {
        super(BungeeEssentials.Mute_MAIN, Permissions.Admin.MUTE, BungeeEssentials.Mute_ALIAS);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args != null && args.length > 0) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
            if (player != null) {
                if (!player.hasPermission(Permissions.Admin.MUTE_EXEMPT)) {
                    if (Messenger.toggleMute(player)) {
                        player.sendMessage(Dictionary.format(Dictionary.MUTE_ENABLED));
                        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                            if (p.hasPermission(Permissions.Admin.RULE_NOTIFY)) {
                                sender.sendMessage(Dictionary.format(Dictionary.MUTE_ENABLEDN));
                            }
                        }
                    } else {
                        player.sendMessage(Dictionary.format(Dictionary.MUTE_DISABLED));
                        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                            if (p.hasPermission(Permissions.Admin.RULE_NOTIFY)) {
                                sender.sendMessage(Dictionary.format(Dictionary.MUTE_DISABLEDN));
                            }
                        }
                    }
                } else {
                    sender.sendMessage(Dictionary.format(Dictionary.MUTE_EXEMPT));
                }
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", BungeeEssentials.Mute_MAIN + " <player>"));
        }
    }
}
