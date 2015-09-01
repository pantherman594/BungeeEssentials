package de.albionco.gssentials.utils;

import com.google.common.base.Joiner;
import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.regex.Rule;
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
        if (BungeeEssentials.getInstance().shouldLog() && logger != null) {
            switch (rule.getHandle()) {
                case ADVERTISEMENT:
                    if (type == Messenger.ChatType.PRIVATE) {
                        logger.log(Level.FINE, "{0} advertised via PM: \"{1}\"", new Object[]{sender.getName(), joiner.join(rule.getMatches())});
                    } else {
                        logger.log(Level.FINE, "{0} advertised in public chat: \"{1}\"", new Object[]{sender.getName(), joiner.join(rule.getMatches())});
                    }
                    break;
                case CURSING:
                    if (type == Messenger.ChatType.PRIVATE) {
                        logger.log(Level.INFO, "{0} cursed via PM: \"{1}\"", new Object[]{sender.getName(), joiner.join(rule.getMatches())});
                    } else {
                        logger.log(Level.INFO, "{0} cursed in public chat: \"{1}\"", new Object[]{sender.getName(), joiner.join(rule.getMatches())});
                    }
                    break;
                case REPLACE:
                    logger.log(Level.WARNING, "{0}''s message was filtered \"{1}\"", new Object[]{sender.getName(), joiner.join(rule.getMatches())});
                    break;
                case COMMAND:
                    logger.log(Level.WARNING, "{0}''s message was filtered \"{1}\"", new Object[]{sender.getName(), joiner.join(rule.getMatches())});
                    break;
			default:
				break;
            }
        }
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
