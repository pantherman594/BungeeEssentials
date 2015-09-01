package de.albionco.gssentials.command.general;

import com.google.common.collect.ImmutableSet;
import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Messenger;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public class JoinCommand extends Command implements TabExecutor {
    public JoinCommand() {
        super(BungeeEssentials.Join_MAIN, Permissions.General.JOIN, BungeeEssentials.Join_ALIAS);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (args == null || args.length < 1) {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", BungeeEssentials.Join_MAIN + " <player>"));
                return;
            }

            ProxiedPlayer player = (ProxiedPlayer) sender;
            ProxiedPlayer join = ProxyServer.getInstance().getPlayer(args[0]);
            if (join == null || Messenger.isHidden(join)) {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_OFFLINE));
                return;
            }

            if (player.getUniqueId() == join.getUniqueId()) {
                sender.sendMessage(ChatColor.RED + "You cannot join yourself!");
                return;
            }

            ServerInfo info = join.getServer().getInfo();
            if (info.canAccess(player)) {
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "Attempting to join " + join.getName() + "'s server..");
                player.connect(info);
            } else {
                sender.sendMessage(ProxyServer.getInstance().getTranslation("no_server_permission"));
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Console cannot join servers");
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
