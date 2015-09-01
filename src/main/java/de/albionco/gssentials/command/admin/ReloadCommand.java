package de.albionco.gssentials.command.admin;

import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ReloadCommand extends Command {
    public ReloadCommand() {
        super(BungeeEssentials.Reload_MAIN, Permissions.Admin.RELOAD, BungeeEssentials.Reload_ALIAS);
    }

    @SuppressWarnings("deprecation")
	@Override
    public void execute(CommandSender sender, String[] args) {
        if (BungeeEssentials.getInstance().reload()) {
            sender.sendMessage(Dictionary.colour("&aBungeeEssentials has been reloaded!"));
        } else {
            sender.sendMessage(Dictionary.colour("&cUnable to reload BungeeEssentials! :("));
        }
    }
}
