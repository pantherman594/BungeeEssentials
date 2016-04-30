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

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Log {
    private static Joiner joiner = Joiner.on(", ");
    private static Logger logger;

    /**
     * Resets the logger.
     */
    static void reset() {
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

    /**
     * Sets up the logger and cleans up the folder.
     *
     * @return Whether logger setup was successful.
     */
    static boolean setup() {
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

    /**
     * Logs broken chat rules.
     *
     * @param sender The player who sent the message.
     * @param rule   The rule the message matched.
     * @param type   The ChatType of the message.
     */
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

            String prefix = "[CHAT/";
            switch (rule.getHandle()) {
                case ADVERTISEMENT:
                    prefix += "ADVERTISEMENT";
                    break;
                case CURSING:
                    prefix += "CURSING";
                    break;
                case REPLACE:
                    prefix += "FILTER";
                    break;
                case COMMAND:
                    prefix += "FILTER";
                    break;
            }
            log(prefix + "] {0} broke a chat rule in " + chatType + ": \"{1}\"", sender.getName(), joiner.join(rule.getMatches()));
        }
    }

    /**
     * Logs specific messages.
     *
     * @param message The message to log.
     * @param args    The arguments.
     */
    public static void log(String message, Object... args) {
        if (logger != null)
            logger.log(Level.FINE, message, args);
    }

    private static class LogFormatter extends Formatter {
        private final SimpleDateFormat format;
        private final Calendar calendar;

        LogFormatter() {
            this.calendar = Calendar.getInstance();
            this.format = new SimpleDateFormat("H:mm:s");
        }

        @Override
        public String format(LogRecord record) {
            return format.format(calendar.getTime()) + " " + MessageFormat.format(record.getMessage(), record.getParameters()) + System.lineSeparator();
        }
    }
}
