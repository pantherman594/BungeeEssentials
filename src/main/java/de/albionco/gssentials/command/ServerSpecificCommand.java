package de.albionco.gssentials.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public abstract class ServerSpecificCommand extends Command {
    private final String permission;

    public ServerSpecificCommand(String name, String permission, String... aliases) {
        super(name, "", aliases);
        this.permission = permission;
    }

    @SuppressWarnings("deprecation")
	@Override
    public final void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(permission)) {
            run(sender, args);
        } else {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            String server = player.getServer().getInfo().getName().toLowerCase().replace(" ", "-");
            if (player.hasPermission(permission + "." + server)) {
                run(sender, args);
            } else {
                player.sendMessage(ProxyServer.getInstance().getTranslation("no_permission"));
            }
        }
    }

    public abstract void run(CommandSender sender, String[] args);
}
