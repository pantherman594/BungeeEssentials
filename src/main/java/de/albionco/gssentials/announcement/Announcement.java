package de.albionco.gssentials.announcement;

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