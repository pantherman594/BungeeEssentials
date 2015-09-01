package de.albionco.gssentials.command.general;

import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.event.MessageEvent;
import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Messenger;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class ReplyCommand extends Command {
    public ReplyCommand() {
        super(BungeeEssentials.Reply_MAIN, Permissions.General.MESSAGE, BungeeEssentials.Reply_ALIAS);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (args.length > 0) {
                ProxiedPlayer player = (ProxiedPlayer) sender;
                UUID uuid = Messenger.reply(player);
                if (uuid == null) {
                    sender.sendMessage(Dictionary.format(Dictionary.ERROR_NOBODY_HAS_MESSAGED));
                    return;
                }
                ProxiedPlayer recipient = ProxyServer.getInstance().getPlayer(uuid);
                ProxyServer.getInstance().getPluginManager().callEvent(new MessageEvent(sender, recipient, Dictionary.combine(args)));
            } else {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", BungeeEssentials.Reply_MAIN + " <message>"));
            }
        } else {
            sender.sendMessage(Dictionary.colour("&cSorry, only players can reply to messages."));
        }
    }
}
