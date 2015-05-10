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

import com.google.common.base.Preconditions;

import java.util.Map;

/**
 * Created by David on 5/8/2015.
 *
 * @author David Shen
 */
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