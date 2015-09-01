package de.albionco.gssentials.utils;

public class Permissions {

    public static class Admin {
        public static final String ALERT = "gssentials.admin.alert";
        public static final String CHAT = "gssentials.admin.chat";
        public static final String SEND = "gssentials.admin.send";
        public static final String SENDALL = "gssentials.admin.send.all";
        public static final String SPY = "gssentials.admin.spy";
        public static final String SPY_EXEMPT = SPY + ".exempt";
        public static final String SPY_COMMAND = SPY + ".command";
        public static final String HIDE = "gssentials.admin.hide";
        public static final String RELOAD = "gssentials.admin.reload";
        public static final String BYPASS_FILTER = "gssentials.admin.bypass-filter";
        public static final String UPDATE = "gssentials.admin.update";
        public static final String LOOKUP = "gssentials.admin.lookup";
        public static final String RULE_NOTIFY = "gssentials.admin.notify";
        public static final String MUTE = "gssentials.admin.mute";
        public static final String MUTE_EXEMPT = MUTE + ".exempt";
        public static final String MUTE_NOTIFY = MUTE + ".notify";
    }

    public static class General {
        public static final String MESSAGE = "gssentials.message";
        public static final String MESSAGE_COLOR = MESSAGE + ".color";
        public static final String FIND = "gssentials.find";
        public static final String LIST = "gssentials.list";
        public static final String LIST_OFFLINE = "gssentials.list.offline";
        public static final String LIST_RESTRICTED = "gssentials.list.restricted";
        public static final String SLAP = "gssentials.slap";
        public static final String JOIN = "gssentials.join";
        public static final String CHAT = "gssentials.chat";
        public static final String IGNORE = "gssentials.ignore";
        public static final String ANNOUNCE = "gssentials.announce";
        public static final String JOINANNC = ANNOUNCE + ".join";
        public static final String QUITANNC = ANNOUNCE + ".quit";
        public static final String ALIAS = "gssentials.alias";
    }

}
