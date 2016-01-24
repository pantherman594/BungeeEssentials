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

package com.pantherman594.gssentials.announcement;

import com.pantherman594.gssentials.BungeeEssentials;
import com.pantherman594.gssentials.Dictionary;
import com.pantherman594.gssentials.Permissions;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class AnnouncementManager {
    private Map<String, Announcement> anncs = new HashMap<>();

    public AnnouncementManager() {
        anncs.clear();
        Configuration anncSection = BungeeEssentials.getInstance().getMessages().getSection("announcements");
        for (String annc : anncSection.getKeys()) {
            int delay = anncSection.getInt(annc + ".delay");
            int interval = anncSection.getInt(annc + ".interval");
            String msg = anncSection.getString(annc + ".message");
            String server = "ALL";
            if (!anncSection.getString(annc + ".server").equals("")) {
                server = anncSection.getString(annc + ".server");
            }
            register(annc, new Announcement(delay, interval, msg, server));
        }
        if (anncs.size() > 0) {
            BungeeEssentials.getInstance().getLogger().log(Level.INFO, "Loaded {0} announcements from config", anncs.size());
            scheduleAnnc();
        }
    }

    private void register(String anncName, Announcement annc) {
        anncs.put(anncName, annc);
    }

    private void scheduleAnnc() {
        for (final String anncName : anncs.keySet()) {
            final Announcement annc = anncs.get(anncName);
            ProxyServer.getInstance().getScheduler().schedule(BungeeEssentials.getInstance(), new Runnable() {
                @Override
                public void run() {
                    annc(annc.getPlayers(), anncName, annc.getMsg());
                    scheduleAnnc(anncName, annc);
                }
            }, annc.getDelay(), TimeUnit.SECONDS);
        }
    }

    private void scheduleAnnc(final String anncName, final Announcement annc) {
        ProxyServer.getInstance().getScheduler().schedule(BungeeEssentials.getInstance(), new Runnable() {
            @Override
            public void run() {
                annc(annc.getPlayers(), anncName, annc.getMsg());
                scheduleAnnc(anncName, annc);
            }
        }, annc.getInterval(), TimeUnit.SECONDS);
    }

    private void annc(Collection<ProxiedPlayer> players, String anncName, String... msg) {
        for (String singMsg : msg) {
            if (!players.isEmpty()) {
                for (ProxiedPlayer p : players) {
                    if (p.hasPermission(Permissions.General.ANNOUNCEMENT) || p.hasPermission(Permissions.General.ANNOUNCEMENT + "." + anncName)) {
                        p.sendMessage(Dictionary.format(Dictionary.FORMAT_ALERT, "MESSAGE", singMsg));
                    }
                }
            }
            ProxyServer.getInstance().getConsole().sendMessage(Dictionary.format(Dictionary.FORMAT_ALERT, "MESSAGE", singMsg));
        }
    }

    public Map<String, Announcement> getAnncs() {
        return anncs;
    }
}
