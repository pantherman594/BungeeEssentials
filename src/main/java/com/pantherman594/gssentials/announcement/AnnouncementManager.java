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
import com.pantherman594.gssentials.utils.Dictionary;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@SuppressWarnings("deprecation")
public class AnnouncementManager {
    private static List<ScheduledTask> tasks = new ArrayList<>();
    private static List<Announcement> anncs = new ArrayList<>();

    public static boolean register(Announcement annc) {
        if (!anncs.contains(annc)) {
            anncs.add(annc);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static boolean load() {
        anncs.clear();
        List<Map<String, String>> section = (List<Map<String, String>>) BungeeEssentials.getInstance().getMessages().getList("announcements");
        int success = 0;
        for (Map<String, String> map : section) {
            Announcement annc = Announcement.deserialize(map);
            if (annc != null) {
                if (register(annc)) {
                    success++;
                }
            }
        }
        if (success > 0) {
            BungeeEssentials.getInstance().getLogger().log(Level.INFO, "Loaded {0} announcements from config", success);
            for (ScheduledTask task : tasks) {
                task.cancel();
            }
            scheduleAnnc();
        }
        return true;
    }

    private static void scheduleAnnc() {
        for (Announcement annc : anncs) {
            int delay = annc.getDelay();
            final int interval = annc.getInterval();
            final String msg = annc.getMsg();
            ProxyServer.getInstance().getScheduler().schedule(BungeeEssentials.getInstance(), new Runnable() {
                @Override
                public void run() {
                    String[] newMsg = msg.split("/n");
                    for (String singMsg : newMsg) {
                        ProxyServer.getInstance().broadcast(Dictionary.format(Dictionary.FORMAT_ALERT, "MESSAGE", singMsg));
                    }
                    scheduleAnnc(interval, newMsg);
                }
            }, delay, TimeUnit.SECONDS);
        }
    }

    private static void scheduleAnnc(final Integer interval, final String[] msg) {
        tasks.add(ProxyServer.getInstance().getScheduler().schedule(BungeeEssentials.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (String singMsg : msg) {
                    ProxyServer.getInstance().broadcast(Dictionary.format(Dictionary.FORMAT_ALERT, "MESSAGE", singMsg));
                }
                scheduleAnnc(interval, msg);
            }
        }, interval, TimeUnit.SECONDS));
    }
}
