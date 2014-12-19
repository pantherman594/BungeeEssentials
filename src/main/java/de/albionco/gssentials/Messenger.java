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

package de.albionco.gssentials;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Created by Connor Harries on 17/10/2014.
 *
 * @author Connor Spencer Harries
 */
@SuppressWarnings("deprecation")
public class Messenger {
    private static WeakHashMap<UUID, UUID> sent = new WeakHashMap<>();
    private static WeakHashMap<UUID, UUID> received = new WeakHashMap<>();

    public static void sendMessage(CommandSender sender, ProxiedPlayer recipient, String message) {
        if (recipient != null) {
            ProxiedPlayer player = null;
            if (sender instanceof ProxiedPlayer) {
                player = (ProxiedPlayer) sender;
            }

            String server = player != null ? player.getServer().getInfo().getName() : "CONSOLE";

            String msg = Dictionary.format(Dictionary.FORMAT_MESSAGE, "SERVER", server, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message, "TIME", getTime(), "RAQUO", "»");

            sender.sendMessage(msg);
            recipient.sendMessage(msg);

            if (player != null) {
                sent.put(player.getUniqueId(), recipient.getUniqueId());
                received.put(recipient.getUniqueId(), player.getUniqueId());
            }
        } else {
            sender.sendMessage(Dictionary.colour(Dictionary.ERRORS_OFFLINE));
        }
    }

    public static String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    public static UUID reply(ProxiedPlayer player) {
        if (received.containsKey(player.getUniqueId())) {
            return received.get(player.getUniqueId());
        }

        if(sent.containsKey(player.getUniqueId())) {
            return sent.get(player.getUniqueId());
        }

        return null;
    }
}
