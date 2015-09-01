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
