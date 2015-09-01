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
public class FindCommand extends Command implements TabExecutor {
    public FindCommand() {
        super(BungeeEssentials.Find_MAIN, Permissions.General.FIND, BungeeEssentials.Find_ALIAS);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);

            if (player != null && !Messenger.isHidden(player)) {
                sender.sendMessage(Dictionary.format(Dictionary.FORMAT_FIND_PLAYER, "SERVER", player.getServer().getInfo().getName(), "PLAYER", player.getName()));
            } else {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_OFFLINE));
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", BungeeEssentials.Find_MAIN + " <player>"));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length > 1 || args.length == 0) {
            return ImmutableSet.of();
        }

        ProxiedPlayer senderPlayer = null;
        if (sender instanceof ProxiedPlayer) {
            senderPlayer = (ProxiedPlayer) sender;
        }
        Set<String> matches = new HashSet<>();
        String search = args[0].toLowerCase();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (senderPlayer != null) {
                if (player.getServer().getInfo().getName().equals(senderPlayer.getServer().getInfo().getName())) {
                    continue;
                }
            }
            if (!player.getName().equals(sender.getName())) {
                if (player.getName().toLowerCase().startsWith(search) && !Messenger.isHidden(player)) {
                    matches.add(player.getName());
                }
            }
        }
        return matches;
    }
}
