package de.albionco.gssentials.command.general;

import com.google.common.collect.ImmutableSet;
import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Messenger;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public class SlapCommand extends Command implements TabExecutor {
    public SlapCommand() {
        super(BungeeEssentials.Slap_MAIN, "", BungeeEssentials.Slap_ALIAS);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(Permissions.General.SLAP)) {
            ProxiedPlayer player = null;
            if (sender instanceof ProxiedPlayer) {
                player = (ProxiedPlayer) sender;
            }
            if (args.length > 0) {
                ProxiedPlayer enemy = ProxyServer.getInstance().getPlayer(args[0]);
                if (enemy != null) {
                    sender.sendMessage(Dictionary.format(Dictionary.SLAPPER_MSG, "SLAPPED", enemy.getName()));
                    enemy.sendMessage(Dictionary.format(Dictionary.SLAPPED_MSG, "SLAPPER", player == null ? "GOD" : player.getName()));
                } else {
                    sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_OFFLINE));
                }
            } else {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", BungeeEssentials.Slap_MAIN + " <player>"));
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_UNWORTHY_OF_SLAP));
        }
    }


    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length > 1 || args.length == 0) {
            return ImmutableSet.of();
        }

        Set<String> matches = new HashSet<>();
        String search = args[0].toLowerCase();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (!player.getName().equals(sender.getName())) {
                if (player.getName().toLowerCase().startsWith(search) && !Messenger.isHidden(player)) {
                    matches.add(player.getName());
                }
            }
        }
        return matches;
    }
}
