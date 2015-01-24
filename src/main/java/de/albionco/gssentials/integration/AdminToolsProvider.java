/*
 * Copyright (c) 2015 Connor Spencer Harries
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.albionco.gssentials.integration;

import de.albionco.gssentials.BungeeEssentials;
import fr.Alphart.BAT.BAT;
import fr.Alphart.BAT.Modules.InvalidModuleException;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.logging.Level;

/**
 * Created by Connor Harries on 24/01/2015.
 *
 * @author Connor Spencer Harries
 */
public class AdminToolsProvider extends IntegrationProvider {
    private boolean enabled = false;

    @Override
    public boolean isMuted(ProxiedPlayer player) {
        if (!enabled) {
            enabled = test();
            if (!enabled) {
                BungeeEssentials.getInstance().getLogger().log(Level.WARNING, "*** \"{0}\" is not enabled ***", getName());
                BungeeEssentials.getInstance().setupIntegration("BungeeAdminTools");
                return false;
            }
        }
        try {
            return BAT.getInstance().getModules().getMuteModule().isMute(player, "(any)") == 1;
        } catch (InvalidModuleException e) {
            return false;
        }
    }

    private boolean test() {
        return BAT.getInstance().getModules() != null;
    }

    @Override
    public String getName() {
        return "BungeeAdminTools";
    }
}
