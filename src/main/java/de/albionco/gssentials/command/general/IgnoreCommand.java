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

/**
 * Created by David on 8/3/2015.
 *
 * @author David
 */
public class IgnoreCommand extends Command implements TabExecutor {
    public IgnoreCommand() {
        super(BungeeEssentials.Ignore_MAIN, Permissions.General.IGNORE, BungeeEssentials.Ignore_ALIAS);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (args.length > 0) {
                if (ProxyServer.getInstance().getPlayer(args[0]) != null && !Messenger.isHidden(ProxyServer.getInstance().getPlayer(args[0]))) {
                    if (ProxyServer.getInstance().getPlayer(args[0]) != sender)
                        Messenger.ignore((ProxiedPlayer) sender, ProxyServer.getInstance().getPlayer(args[0]));
                    else sender.sendMessage(Dictionary.format(Dictionary.ERROR_IGNORE_SELF));
                } else sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_OFFLINE));
            } else {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS));
            }
        } else {
            sender.sendMessage(Dictionary.colour("&cConsole cannot ignore players (how are you seeing messages in the first place?)"));
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
