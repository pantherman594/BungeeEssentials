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

package de.albionco.gssentials.utils;

/**
 * This class was made so anybody with the intention of
 * editing the plugin can easily do so without having
 * to scour the source for the correct permissions
 *
 * @author Connor Spencer Harries
 */
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
    }

}
