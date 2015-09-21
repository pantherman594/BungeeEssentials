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

import managers.BansManager;
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
    public boolean isBanned(ProxiedPlayer player) {
        return BansManager.isPlayerBanned(player.getName());
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
