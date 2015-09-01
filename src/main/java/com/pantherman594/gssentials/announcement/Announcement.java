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

import com.google.common.base.Preconditions;

import java.util.Map;

public class Announcement {
    private Integer delay = null;
    private Integer interval = null;
    private String msg = null;

    public static Announcement deserialize(Map<String, String> serialized) {
        Preconditions.checkNotNull(serialized);
        Preconditions.checkArgument(!serialized.isEmpty());
        Preconditions.checkNotNull(serialized.get("delay"), "invalid delay");
        Preconditions.checkNotNull(serialized.get("interval"), "invalid interval");
        Preconditions.checkNotNull(serialized.get("message"), "invalid message");

        Announcement annc = new Announcement();
        annc.delay(Integer.parseInt(String.valueOf(serialized.get("delay"))));
        annc.interval(Integer.parseInt(String.valueOf(serialized.get("interval"))));
        annc.msg(String.valueOf(serialized.get("message")));
        return annc;
    }

    private Announcement delay(Integer delay) {
        this.delay = delay;
        return this;
    }

    private Announcement interval(Integer interval) {
        this.interval = interval;
        return this;
    }

    private Announcement msg(String msg) {
        this.msg = msg;
        return this;
    }

    public Integer getDelay() {
        return delay;
    }

    public Integer getInterval() {
        return interval;
    }

    public String getMsg() {
        return msg;
    }
}