package de.albionco.gssentials.aliases;

import de.albionco.gssentials.BungeeEssentials;
import net.md_5.bungee.api.ProxyServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class AliasManager {
    public static List<Alias> aliases = new ArrayList<>();

    public static boolean register(Alias alias) {
        if (!aliases.contains(alias)) {
            aliases.add(alias);
            ProxyServer.getInstance().getPluginManager().registerCommand(BungeeEssentials.getInstance(), new LoadCmds(alias.getAlias(), alias.getCommands()));
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static boolean load() {
        aliases.clear();
        List<Map<String, String>> section = (List<Map<String, String>>) BungeeEssentials.getInstance().getConfig().getList("aliases");
        int success = 0;
        for (Map<String, String> map : section) {
            Alias alias = Alias.deserialize(map);
            if (alias != null) {
                if (register(alias)) {
                    success++;
                }
            }
        }
        if (success > 0) {
            BungeeEssentials.getInstance().getLogger().log(Level.INFO, "Loaded {0} aliases from config", success);
        }
        return true;
    }
}
