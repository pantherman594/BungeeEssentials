package de.albionco.gssentials.command.admin;

import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public class LookupCommand extends Command {
    public LookupCommand() {
        super(BungeeEssentials.Lookup_MAIN, Permissions.Admin.LOOKUP, BungeeEssentials.Lookup_ALIAS);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Set<String> matches = new HashSet<>();
        if (args.length > 0) {
            String partialPlayerName = args[0].toLowerCase();
            for (String p : BungeeEssentials.getInstance().getPlayerConfig().getStringList("players")) {
                if (p.toLowerCase().startsWith(partialPlayerName.toLowerCase())) {
                    matches.add(p);
                }
            }
            sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_HEADER, "SIZE", String.valueOf(matches.size())));
            for (String match : matches) {
                sender.sendMessage(Dictionary.format(Dictionary.LOOKUP_BODY, "PLAYER", match));
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP", BungeeEssentials.Lookup_MAIN + " <beginning of name>"));
        }
    }
}
