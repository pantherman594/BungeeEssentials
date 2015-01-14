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

package de.albionco.gssentials;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;

/**
 * Created by Connor Harries on 17/10/2014.
 *
 * @author Connor Spencer Harries
 */
public class Dictionary {
    public static String FORMAT_ADMIN = "&c[{{ SERVER }}, {{ SENDER }}] &7{{ FORMAT_MESSAGE }}";
    public static String FORMAT_ALERT = "&8[&a+&8] &7{{ FORMAT_ALERT }}";
    public static String FORMAT_FIND = "&e{{ PLAYER }} &ais playing on &e{{ SERVER }}";
    public static String FORMAT_SERVERS_HEADER = "&aServers:";
    public static String FORMAT_SERVERS_BODY = "&a- {{ SERVER }} {{ DENSITY }}";
    public static String ERRORS_INVALID = "&cInvalid arguments provided.";
    public static String FORMAT_MESSAGE = "&a({{ SERVER }}) &7[{{ SENDER }} » {{ RECIPIENT }}] &f{{{  FORMAT_MESSAGE  }}}";
    public static String ERRORS_OFFLINE = "&cSorry, that player is offline.";
    public static String ERRORS_MESSAGES = "&cNobody has messaged you recently.";
    public static String ERRORS_SLAP = "&cYou are unworthy of slapping people.";
    public static String FORMAT_SPY_MESSAGE = "&a({{ SERVER }}) &7[{{ SENDER }} » {{ RECIPIENT }}] &f{{{  FORMAT_MESSAGE  }}}";
    public static String FORMAT_SEND = "&aSending &e{{ PLAYER }} &ato server &e{{ SERVER }}";
    public static String FORMAT_SPY_ENABLED = "&aSocialspy has been enabled!";
    public static String FORMAT_SPY_DISABLED = "&cSocialspy has been disabled!";

    public static String colour(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static String combine(String[] array) {
        StringBuilder builder = new StringBuilder();
        for (String string : array) {
            builder.append(string);
            if (!string.equals(array[array.length - 1])) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    public static String combine(int omit, String[] array) {
        StringBuilder builder = new StringBuilder();
        for (String string : array) {
            if (!string.equals(array[omit])) {
                builder.append(string);
                if (!string.equals(array[array.length - 1])) {
                    builder.append(" ");
                }
            }
        }
        return builder.toString();
    }

    public static String format(String input, boolean colour, String... args) {
        input = input.replace("{{ TIME }}", getTime());
        input = input.replace("{{ RAQUO }}", "»");
        if (args.length % 2 == 0) {
            for (int i = 0; i < args.length; i += 2) {
                input = input.replace("{{ " + args[i].toUpperCase() + " }}", args[i + 1]);
            }
        }

        return colour ? colour(input) : input;
    }

    public static String format(String input, String... args) {
        return Dictionary.format(input, true, args);
    }

    public static void load() throws IllegalAccessException {
        Configuration config = BungeeEssentials.getInstance().getConfig();
        for (Field field : Dictionary.class.getDeclaredFields()) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) && Modifier.isPublic(mod)) {
                String name = field.getName().toLowerCase().replace("_", ".");

                String value = config.getString(name, "Please see the BungeeEssentials default config");
                if (value.equals("Please see the BungeeEssentials default config")) {
                    BungeeEssentials.getInstance().getLogger().log(Level.WARNING, "Error loading \"{0}\" from configuration file", name);
                }
                field.set(null, value);
            }
        }
    }

    private static String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }
}
