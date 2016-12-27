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

import net.md_5.bungee.api.CommandSender;

@SuppressWarnings("WeakerAccess")
public class Permissions {

    public static boolean hasPerm(CommandSender sender, String permission) {
        while (permission.split("\\.").length > 2) {
            if (sender.hasPermission("-" + permission)) {
                return false;
            }
            if (sender.hasPermission(permission)) {
                return true;
            }
            permission = permission.replaceAll("\\.[^.]+(\\.\\*)?$", ".*");
        }
        return sender.hasPermission("*");
    }

    public static boolean hasPerm(CommandSender sender, String... permissions) {
        for (String permission : permissions) {
            if (hasPerm(sender, permission)) {
                return true;
            }
        }
        return false;
    }

    public static class Admin {
        public static final String PREFIX = "gssentials.admin.";
        public static final String ALERT = PREFIX + "alert";
        public static final String CHAT = PREFIX + "chat";
        public static final String SEND = PREFIX + "send";
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
        public static final String LOOKUP_INFO = LOOKUP + ".info";
        public static final String LOOKUP_IP = LOOKUP_INFO + ".ip";
        public static final String LOOKUP_SPY = LOOKUP_INFO + ".spy";
        public static final String LOOKUP_HIDDEN = LOOKUP_INFO + ".hidden";
        public static final String LOOKUP_ALL = LOOKUP_INFO + ".all";
        public static final String NOTIFY = PREFIX + "notify";
        public static final String MUTE = PREFIX + "mute";
        public static final String MUTE_EXEMPT = MUTE + ".exempt";
        public static final String HOVER_LIST = PREFIX + "hover-list";
        public static final String MSGGROUP = PREFIX + "gmessage";
        public static final String MG_FORCE_JOIN = MSGGROUP + ".forcejoin";
        public static final String MG_MAKE_OWNER = MSGGROUP + ".makeowner";
        public static final String MG_KICK = MSGGROUP + ".kick";
        public static final String MG_DISBAND = MSGGROUP + ".disband";
        public static final String MG_ALL = MSGGROUP + ".all";
    }

    public static class General {
        public static final String PREFIX = "gssentials.";
        public static final String MESSAGE = PREFIX + "message";
        public static final String MESSAGE_GLOBAL = MESSAGE + ".global";
        public static final String MESSAGE_COLOR = MESSAGE + ".color";
        public static final String MESSAGE_HOVER = MESSAGE + ".hover";
        public static final String MESSAGE_CLICK = MESSAGE + ".click";
        public static final String FIND = PREFIX + "find";
        public static final String LIST = PREFIX + "list";
        public static final String LIST_OFFLINE = LIST + ".offline";
        public static final String LIST_RESTRICTED = LIST + ".restricted";
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
        public static final String MSGGROUP = PREFIX + "gmessage";
        public static final String MG_CREATE = MSGGROUP + ".create";
    }

}
