/*
 * Copyright (c) 2014 Connor Spencer Harries
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

package me.hadrondev;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Created by Connor Harries on 17/10/2014.
 */
@SuppressWarnings("deprecation")
public class Chat {
    private static WeakHashMap<UUID, UUID> messages = new WeakHashMap<>();

    public static void sendMessage(ProxiedPlayer player, String name, String message) {
        ProxiedPlayer recipient = BungeeEssentials.me.getProxy().getPlayer(name);
        if(recipient != null) {
            String msg = Settings.MESSAGE;
            msg = msg.replace("{SERVER}", player.getServer().getInfo().getName());
            msg = msg.replace("{SENDER}", player.getName());
            msg = msg.replace("{RECIPIENT}", recipient.getName());
            msg = msg.replace("{MESSAGE}", message);
            msg = msg.replace("{TIME}", getTime());

            msg = Settings.colour(msg);

            player.sendMessage(msg);
            recipient.sendMessage(msg);

            messages.put(recipient.getUniqueId(), player.getUniqueId());
        } else {
            player.sendMessage(Settings.colour(Settings.PLAYER_OFFLINE));
        }
    }

    public static String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    public static UUID reply(ProxiedPlayer player) {
        for(Map.Entry<UUID, UUID> uuid : messages.entrySet()) {
            if(uuid.getKey() == player.getUniqueId()) {
                return uuid.getValue();
            }
        }
        return null;
    }
}
