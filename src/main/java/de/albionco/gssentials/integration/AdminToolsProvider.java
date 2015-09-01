package de.albionco.gssentials.integration;

import fr.Alphart.BAT.BAT;
import fr.Alphart.BAT.Modules.InvalidModuleException;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AdminToolsProvider extends IntegrationProvider {

    @Override
    public boolean isMuted(ProxiedPlayer player) {
        try {
            return BAT.getInstance().getModules().getMuteModule().isMute(player, "(any)") == 1;
        } catch (InvalidModuleException e) {
            return false;
        }
    }

    @Override
    public boolean isEnabled() {
        try {
            return BAT.getInstance() != null && BAT.getInstance().getModules() != null && BAT.getInstance().getModules().getMuteModule() != null;
        } catch (InvalidModuleException ex) {
            return false;
        }
    }

    @Override
    public String getName() {
        return "BungeeAdminTools";
    }
}
