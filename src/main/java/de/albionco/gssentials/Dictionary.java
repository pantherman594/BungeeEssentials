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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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
    private static final String DEFAULT_CONFIG_VALUE = "INVALID CONFIGURATION VALUE";
    @Load(key = "errors.messages", def = "&cNobody has messaged you recently.")
    public static String ERROR_NOBODY_HAS_MESSAGED;
    @Load(key = "errors.slap", def = "&cYou are unworthy of slapping people.")
    public static String ERROR_UNWORTHY_OF_SLAP;
    @Load(key = "errors.offline", def = "&cSorry, that player is offline.")
    public static String ERROR_PLAYER_OFFLINE;
    @Load(key = "errors.invalid", def = "&cInvalid arguments provided.")
    public static String ERROR_INVALID_ARGUMENTS;
    @Load(key = "format.message", def = "&a({{ SERVER }}) &7[{{ SENDER }} » {{ RECIPIENT }}] &f{{{ MESSAGE }}}")
    public static String FORMAT_PRIVATE_MESSAGE;
    @Load(key = "format.admin", def = "&c[{{ SERVER }}, {{ SENDER }}] &7{{ FORMAT_MESSAGE }}")
    public static String FORMAT_STAFF_CHAT;
    @Load(key = "format.send", def = "&aSending &e{{ PLAYER }} &ato server &e{{ SERVER }}")
    public static String FORMAT_SEND_PLAYER;
    @Load(key = "format.find", def = "&e{{ PLAYER }} &ais playing on &e{{ SERVER }}")
    public static String FORMAT_FIND_PLAYER;
    @Load(key = "format.alert", def = "&8[&a+&8] &7{{ FORMAT_ALERT }}")
    public static String FORMAT_ALERT;
    @Load(key = "list.header", def = "&aServers")
    public static String LIST_HEADER;
    @Load(key = "list.body", def = "&a- {{ SERVER }} {{ DENSITY }}")
    public static String LIST_BODY;
    @Load(key = "spy.message", def = "&a({{ SERVER }}) &7[{{ SENDER }} » {{ RECIPIENT }}] &f{{{ MESSAGE }}}")
    public static String SPY_MESSAGE;
    @Load(key = "spy.enabled", def = "&aSocialspy has been enabled!")
    public static String SPY_ENABLED;
    @Load(key = "spy.disabled", def = "&cSocialspy has been disabled!")
    public static String SPY_DISABLED;
    @Load(key = "hide.enabled", def = "&aYou are now hidden from all users!")
    public static String HIDE_ENABLED;
    @Load(key = "hide.disabled", def = "&cYou are no longer hidden!")
    public static String HIDE_DISABLED;
    @Load(key = "warnings.similarity", def = "&cPlease do not spam other players!")
    public static String WARNING_LEVENSHTEIN_DISTANCE;
    @Load(key = "warnings.swearing", def = "&cPlease do not swear at other players!")
    public static String WARNING_HANDLE_CURSING;
    @Load(key = "warnings.advertising", def = "&cPlease do not advertise other servers!")
    public static String WARNINGS_ADVERTISING;
    @Load(key = "multilog.kicked", def = "&cMaximum number of connections reached!")
    public static String MULTILOG_KICK_MESSAGE;
    private static SimpleDateFormat date;

    private static Calendar calendar;

    static {
        calendar = Calendar.getInstance();
        date = new SimpleDateFormat("HH:mm:ss");
    }

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
        // Minor fix for people who suffer from encoding issues
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

    /**
     * Use reflection and the {@link Load} annotation to load values
     * from the configuration file without typing out all the keys
     *
     * @throws IllegalAccessException
     */
    public static void load() throws IllegalAccessException {
        Configuration config = BungeeEssentials.getInstance().getConfig();
        for (Field field : Dictionary.class.getDeclaredFields()) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) && Modifier.isPublic(mod) && field.isAnnotationPresent(Load.class)) {
                Load load = field.getAnnotation(Load.class);
                String value = config.getString(load.key(), DEFAULT_CONFIG_VALUE);
                if (value.equals(DEFAULT_CONFIG_VALUE)) {
                    /*
                     * Why not just load the default value?
                     * That doesn't let the admin know that there's a problem,
                     * this is my personal preference and if you wish to edit
                     * it to use the default value immediately then please do.
                     *
                     * Once the admin is notified of the problem THEN we fall
                     * back to the default value specified anyway.
                     */
                    BungeeEssentials.getInstance().getLogger().log(Level.WARNING, "Your configuration is either outdated or invalid!");
                    BungeeEssentials.getInstance().getLogger().log(Level.WARNING, "Falling back to default value for key \"{0}\"", load.key());
                    value = load.def();
                }
                field.set(null, value);
            }
        }
    }

    private static String getTime() {
        return date.format(calendar.getTime());
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Load {
        String key();

        String def();
    }
}
