package de.albionco.gssentials.integration;

import de.albionco.gssentials.BungeeEssentials;

import java.util.logging.Level;

public class IntegrationTest implements Runnable {
    @Override
    public void run() {
        if (BungeeEssentials.getInstance().isIntegrated() || BungeeEssentials.getInstance().getIntegrationProvider() != null) {
            IntegrationProvider provider = BungeeEssentials.getInstance().getIntegrationProvider();
            if (!provider.isEnabled()) {
                BungeeEssentials.getInstance().getLogger().log(Level.WARNING, "*** \"{0}\" is not enabled ***", provider.getName());
                BungeeEssentials.getInstance().setupIntegration(provider.getName());
            }
        }
    }
}
