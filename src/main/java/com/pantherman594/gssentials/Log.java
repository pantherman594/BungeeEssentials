/*
 * BungeeEssentials: Full customization of a few necessary features for your server!
 * Copyright (C) 2015  David Shen (PantherMan594)
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

import com.google.common.base.Joiner;
import com.pantherman594.gssentials.regex.Rule;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.*;

public class Log {
    private static Joiner joiner = Joiner.on(", ");
    private static Logger logger;

    public static void reset() {
        if (logger != null) {
            // If the logger is null it's because the plugin is loading for the first time
            for (Handler handler : logger.getHandlers()) {
                if (handler instanceof FileHandler) {
                    handler.close();
                    logger.removeHandler(handler);
                }
            }
        }
        logger = null;
    }

    public static boolean setup() {
        File logDir = new File(BungeeEssentials.getInstance().getDataFolder(), "chat");
        File logFile = new File(logDir, "chat.log");
        if (!logDir.exists()) {
            logDir.mkdir();
        }

        if (logFile.exists()) {
            try {
                if (logFile.length() > 0) {
                    Files.move(logFile.toPath(), new File(logDir, "chat-" + System.currentTimeMillis() + ".log").toPath());
                    BungeeEssentials.getInstance().getLogger().log(Level.INFO, "Moved old log file to \"chat\" directory");
                }
            } catch (IOException e) {
                // ignored, not much we can do.
            }
        }

        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Logger constructing = java.util.logging.Logger.getLogger(BungeeEssentials.class.getSimpleName() + "ChatLogger");
        constructing.setUseParentHandlers(false);

        FileHandler handler;
        try {
            handler = new FileHandler(logFile.getPath(), logFile.exists());
            handler.setFormatter(new LogFormatter());
            constructing.addHandler(handler);
            logger = constructing;
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static void log(ProxiedPlayer sender, Rule rule, Messenger.ChatType type) {
        if (BungeeEssentials.getInstance().contains("log") && logger != null) {
            String chatType = "";
            switch (type) {
                case PRIVATE:
                    chatType = "a PM";
                    break;
                case PUBLIC:
                    chatType = "public chat";
                    break;
                case GLOBAL:
                    chatType = "global chat";
                    break;
                case STAFF:
                    chatType = "staff chat";
                    break;
            }
            if (!chatType.equals("")) {
                log("{0} broke a chat rule in " + chatType + ": \"{1}\"", sender.getName(), joiner.join(rule.getMatches()));
            }
        }
    }

    public static void log(String message, Object... args) {
        logger.log(Level.FINE, message, args);
    }

    private static class LogFormatter extends Formatter {
        private final SimpleDateFormat format;
        private final Calendar calendar;

        public LogFormatter() {
            this.calendar = Calendar.getInstance();
            this.format = new SimpleDateFormat("H:mm:s");
        }

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            builder.append(format.format(calendar.getTime()));
            builder.append(" ");
            builder.append("[");
            builder.append("Chat");
            builder.append("/");

            if (record.getLevel() == Level.FINE) {
                builder.append("ADVERTISEMENT");
            } else if (record.getLevel() == Level.INFO) {
                builder.append("CURSING");
            } else if (record.getLevel() == Level.WARNING) {
                builder.append("FILTER");
            } else {
                builder.append("OTHER");
            }

            builder.append("]");
            builder.append(" ");
            builder.append(MessageFormat.format(record.getMessage(), record.getParameters()));
            builder.append(System.lineSeparator());
            return builder.toString();
        }
    }
}
