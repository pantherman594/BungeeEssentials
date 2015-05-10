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

package de.albionco.gssentials.announcement;

import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.Dictionary;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by David on 5/8/2015.
 *
 * @author David Shen
 */
@SuppressWarnings("deprecation")
public class AnncsManager {
    private static List<ScheduledTask> tasks = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public static boolean load() {
        Anncs.annc_Interval = new HashMap<>();
        Anncs.annc_Msg = new HashMap<>();
        List<Map<String, String>> section = (List<Map<String, String>>) BungeeEssentials.getInstance().getConfig().getList("announcements");
        for (Map<String, String> map : section) {
            Anncs.deserialize(map);
        }
        int success = Anncs.annc_Interval.size();
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
        for (Integer num : Anncs.annc_Msg.keySet()) {
            int delay = Anncs.annc_Delay.get(num);
            final int interval = Anncs.annc_Interval.get(num);
            final String msg = Anncs.annc_Msg.get(num);
            ProxyServer.getInstance().getScheduler().schedule(BungeeEssentials.getInstance(), new Runnable() {
                @Override
                public void run() {
                    ProxyServer.getInstance().broadcast(Dictionary.format(Dictionary.FORMAT_ALERT, "MESSAGE", msg));
                    scheduleAnnc(interval, msg);
                }
            }, delay, TimeUnit.SECONDS);
        }
    }

    private static void scheduleAnnc(final Integer interval, final String msg) {
        tasks.add(ProxyServer.getInstance().getScheduler().schedule(BungeeEssentials.getInstance(), new Runnable() {
            @Override
            public void run() {
                ProxyServer.getInstance().broadcast(Dictionary.format(Dictionary.FORMAT_ALERT, "MESSAGE", msg));
                scheduleAnnc(interval, msg);
            }
        }, interval, TimeUnit.SECONDS));
    }
}
