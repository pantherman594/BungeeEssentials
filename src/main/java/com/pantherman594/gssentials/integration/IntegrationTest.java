/*
 * BungeeEssentials: Full customization of a few necessary features for your server!
 * Copyright (C) 2015  David Shen (PantherMan594)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pantherman594.gssentials.integration;

import com.pantherman594.gssentials.BungeeEssentials;

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
