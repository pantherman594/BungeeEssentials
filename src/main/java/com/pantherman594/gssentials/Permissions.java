/*
 * BungeeEssentials: Full customization of a few necessary features for your server!
 * Copyright (C) 2016 David Shen (PantherMan594)
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

package com.pantherman594.gssentials;

@SuppressWarnings("WeakerAccess")
public class Permissions {

    public static class Admin {
        public static final String PREFIX = "gssentials.admin.";
        public static final String ALERT = PREFIX + "alert";
        public static final String CHAT = PREFIX + "chat";
        public static final String SEND = PREFIX + "asend";
        public static final String SENDALL = PREFIX + "sendall";
        public static final String SPY = PREFIX + "spy";
        public static final String SPY_EXEMPT = SPY + ".exempt";
        public static final String SPY_COMMAND = SPY + ".command";
        public static final String HIDE = PREFIX + "hide";
        public static final String SEE_HIDDEN = HIDE + ".bypass";
        public static final String RELOAD = PREFIX + "reload";
        public static final String BYPASS_FILTER = PREFIX + "bypass-filter";
        public static final String BYPASS_MSG = General.MESSAGE + ".bypass";
        public static final String LOOKUP = PREFIX + "lookup";
        public static final String NOTIFY = PREFIX + "notify";
        public static final String MUTE = PREFIX + "mute";
        public static final String MUTE_EXEMPT = MUTE + ".exempt";
    }

    public static class General {
        public static final String PREFIX = "gssentials.";
        public static final String MESSAGE = PREFIX + "message";
        public static final String MESSAGE_COLOR = MESSAGE + ".color";
        public static final String MESSAGE_HOVER = MESSAGE + ".hover";
        public static final String MESSAGE_CLICK = MESSAGE + ".click";
        public static final String FIND = PREFIX + "find";
        public static final String LIST = PREFIX + "list";
        public static final String LIST_OFFLINE = PREFIX + "list.offline";
        public static final String LIST_RESTRICTED = PREFIX + "list.restricted";
        public static final String SLAP = PREFIX + "slap";
        public static final String JOIN = PREFIX + "join";
        public static final String CHAT = PREFIX + "chat";
        public static final String IGNORE = PREFIX + "ignore";
        public static final String ANNOUNCE = PREFIX + "announce";
        public static final String JOINANNC = ANNOUNCE + ".join";
        public static final String QUITANNC = ANNOUNCE + ".quit";
        public static final String ALIAS = PREFIX + "alias";
        public static final String ANNOUNCEMENT = PREFIX + "announcement";
        public static final String FRIEND = PREFIX + "friend";
    }

}
