package de.albionco.gssentials.command.admin;

import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.command.ServerSpecificCommand;
import de.albionco.gssentials.event.StaffChatEvent;
import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Messenger;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@SuppressWarnings("deprecation")
public class ChatCommand extends ServerSpecificCommand {
    public ChatCommand() {
        super(BungeeEssentials.StaffChat_MAIN, Permissions.Admin.CHAT, BungeeEssentials.StaffChat_ALIAS);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args != null && args.length > 0) {
            String server = "CONSOLE";

            if (sender instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) sender;
                server = player.getServer().getInfo().getName();
                if (args.length == 1) {
                    if (args[0].equals("on")) {
                        Messenger.enableStaffChat(player);
                        player.sendMessage(Dictionary.format(Dictionary.SCHAT_ENABLED));
                        return;
                    } else if (args[0].equals("off")) {
                        Messenger.disableStaffChat(player);
                        player.sendMessage(Dictionary.format(Dictionary.SCHAT_DISABLED));
                        return;
                    }
                }
            }

            String msg;
            if (sender instanceof ProxiedPlayer) {
                msg = Messenger.filter((ProxiedPlayer) sender, Dictionary.combine(args));
            } else {
                msg = Dictionary.combine(args);
            }

            ProxyServer.getInstance().getPluginManager().callEvent(new StaffChatEvent(server, sender.getName(), msg));

        } else {
            ProxiedPlayer player;
            if (sender instanceof ProxiedPlayer) {
                player = (ProxiedPlayer) sender;
                if (Messenger.toggleStaffChat((ProxiedPlayer) sender)) {
                    player.sendMessage(Dictionary.format(Dictionary.SCHAT_ENABLED));
                } else {
                    player.sendMessage(Dictionary.format(Dictionary.SCHAT_DISABLED));
                }
            } else {
                sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", BungeeEssentials.StaffChat_MAIN + " [on|off]"));
            }
        }
    }
}
