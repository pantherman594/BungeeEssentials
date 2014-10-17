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

import net.md_5.bungee.api.ChatColor;

/**
 * Created by Connor Harries on 17/10/2014.
 */
public class Settings {
    public static String ALERT = "&8[&a+&8] &7{ALERT}";
    public static String FIND = "&e{PLAYER} &ais playing on &e{SERVER}";
    public static String GLIST_HEADER = "&aServers:";
    public static String GLIST_SERVER = "&a- {SERVER} {DENSITY}";
    public static String INVALID_ARGS = "&cInvalid arguments provided.";
    public static String MESSAGE = "&a({SERVER}) &7[{SENDER} » {RECIPIENT}] &f{MESSAGE}";
    public static String PLAYER_OFFLINE = "&cSorry, that player is offline.";
    public static String NO_PERMS = "&cYou do not have permission to do that.";
    public static String NO_SLAP = "&cYou are unworthy of slapping people.";
    public static String SEND = "&aSending &e{PLAYER} &ato server &e{SERVER}";

    public static String colour(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
