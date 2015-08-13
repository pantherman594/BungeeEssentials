package de.albionco.gssentials.event;

import de.albionco.gssentials.BungeeEssentials;
import de.albionco.gssentials.regex.RuleManager;
import de.albionco.gssentials.utils.Dictionary;
import de.albionco.gssentials.utils.Log;
import de.albionco.gssentials.utils.Messenger;
import de.albionco.gssentials.utils.Permissions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by David on 8/3/2015.
 *
 * @author David
 */
public class MessageEvent extends Event {
    private CommandSender sender;
    private ProxiedPlayer recipient;
    private String msg;

    public MessageEvent(CommandSender sender, ProxiedPlayer recipient, String msg) {
        this.sender = sender;
        this.recipient = recipient;
        this.msg = msg;
        String message = msg;
        if (recipient != null && !Messenger.isHidden(recipient)) {
            ProxiedPlayer player = null;
            if (sender instanceof ProxiedPlayer) {
                player = (ProxiedPlayer) sender;
                if (BungeeEssentials.getInstance().isIntegrated() && (BungeeEssentials.getInstance().getIntegrationProvider() != null && BungeeEssentials.getInstance().getIntegrationProvider().isMuted((ProxiedPlayer) sender))) {
                    sender.sendMessage(ChatColor.RED + "You are muted and cannot message other players!");
                    return;
                }
                if (!sender.hasPermission(Permissions.Admin.BYPASS_FILTER) && BungeeEssentials.getInstance().useRules()) {
                    List<RuleManager.MatchResult> results = RuleManager.matches(msg);
                    for (RuleManager.MatchResult result : results) {
                        if (result.matched()) {
                            Log.log(player, result.getRule(), Messenger.ChatType.PRIVATE);
                            switch (result.getRule().getHandle()) {
                                case ADVERTISEMENT:
                                    sender.sendMessage(Dictionary.format(Dictionary.WARNINGS_ADVERTISING));
                                    Messenger.ruleNotify(Dictionary.NOTIFY_ADVERTISEMENT, (ProxiedPlayer) sender, msg);
                                    return;
                                case CURSING:
                                    sender.sendMessage(Dictionary.format(Dictionary.WARNING_HANDLE_CURSING));
                                    Messenger.ruleNotify(Dictionary.NOTIFY_CURSING, (ProxiedPlayer) sender, msg);
                                    return;
                                case REPLACE:
                                    if (result.getRule().getReplacement() != null && message != null) {
                                        Matcher matcher = result.getRule().getPattern().matcher(message);
                                        message = matcher.replaceAll(result.getRule().getReplacement());
                                    }
                                    Messenger.ruleNotify(Dictionary.NOTIFY_REPLACE, (ProxiedPlayer) sender, msg);
                                    break;
                                case COMMAND:
                                    CommandSender console = ProxyServer.getInstance().getConsole();
                                    String command = result.getRule().getCommand();
                                    if (command != null) {
                                        ProxyServer.getInstance().getPluginManager().dispatchCommand(console, command.replace("{{ SENDER }}", sender.getName()));
                                    }
                                    Messenger.ruleNotify(Dictionary.NOTIFY_COMMAND, (ProxiedPlayer) sender, msg);
                                    return;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }

            String server = player != null ? player.getServer().getInfo().getName() : "CONSOLE";
            if (player != null) {
                if (BungeeEssentials.getInstance().useSpamProtection() && !player.hasPermission(Permissions.Admin.BYPASS_FILTER)) {
                    if (Messenger.sentMessages.get(player.getUniqueId()) != null) {
                        String last = Messenger.sentMessages.get(player.getUniqueId());
                        if (Messenger.compare(msg, last) > 0.85) {
                            sender.sendMessage(Dictionary.format(Dictionary.WARNING_LEVENSHTEIN_DISTANCE));
                            return;
                        }
                    }
                    Messenger.sentMessages.put(player.getUniqueId(), msg);
                }

                if (!sender.hasPermission(Permissions.Admin.SPY_EXEMPT)) {
                    String spyMessage = Dictionary.format(Dictionary.SPY_MESSAGE, "SERVER", server, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message);
                    for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                        if (player.getUniqueId() != onlinePlayer.getUniqueId() && recipient.getUniqueId() != onlinePlayer.getUniqueId()) {
                            if (onlinePlayer.hasPermission(Permissions.Admin.SPY) && Messenger.isSpy(onlinePlayer)) {
                                onlinePlayer.sendMessage(spyMessage);
                            }
                        }
                    }
                }
                Messenger.messages.put(recipient.getUniqueId(), player.getUniqueId());
            }
            if (BungeeEssentials.getInstance().ignore()) {
                if (!Messenger.isIgnoring((ProxiedPlayer) sender, recipient)) {
                    if (!Messenger.isIgnoring(recipient, (ProxiedPlayer) sender)) {
                        sender.sendMessage(Dictionary.formatMsg(Dictionary.FORMAT_PRIVATE_MESSAGE, "SERVER", recipient.getServer().getInfo().getName(), "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));
                        recipient.sendMessage(Dictionary.formatMsg(Dictionary.FORMAT_PRIVATE_MESSAGE, "SERVER", server, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));
                    }
                } else {
                    sender.sendMessage(Dictionary.format(Dictionary.ERROR_IGNORING));
                }
            } else {
                sender.sendMessage(Dictionary.formatMsg(Dictionary.FORMAT_PRIVATE_MESSAGE, "SERVER", recipient.getServer().getInfo().getName(), "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));
                recipient.sendMessage(Dictionary.formatMsg(Dictionary.FORMAT_PRIVATE_MESSAGE, "SERVER", server, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));
            }
        } else {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_OFFLINE));
        }
    }

    public CommandSender getSender() {
        return sender;
    }

    public ProxiedPlayer getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return msg;
    }
}
