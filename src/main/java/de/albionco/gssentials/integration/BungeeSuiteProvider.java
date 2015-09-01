package de.albionco.gssentials.integration;

import managers.PlayerManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import objects.BSPlayer;

public class BungeeSuiteProvider extends IntegrationProvider {
    @Override
    public boolean isMuted(ProxiedPlayer player) {
        BSPlayer suitePlayer = PlayerManager.getPlayer(player);
        return suitePlayer != null && suitePlayer.isMuted();
    }

    @Override
    public boolean isEnabled() {
        return ProxyServer.getInstance().getChannels().contains("BSChat");
    }

    @Override
    public String getName() {
        return "BungeeSuite";
    }
}
