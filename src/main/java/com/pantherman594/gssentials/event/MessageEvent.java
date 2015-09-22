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

package com.pantherman594.gssentials.event;

import com.pantherman594.gssentials.BungeeEssentials;
import com.pantherman594.gssentials.regex.RuleManager;
import com.pantherman594.gssentials.utils.Dictionary;
import com.pantherman594.gssentials.utils.Log;
import com.pantherman594.gssentials.utils.Messenger;
import com.pantherman594.gssentials.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

import java.util.List;
import java.util.regex.Matcher;

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
                if (Messenger.isMutedF(player, msg)) {
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
                    if (message != null) {
                        for (String word : BungeeEssentials.getInstance().getMessages().getStringList("bannedwords.list")) {
                            String finalReg = "\\b(";
                            for (char l : word.toCharArray()) {
                                finalReg += l + "(\\W|\\d|_)*";
                            }
                            finalReg += ")";
                            if (!finalReg.equals("\\b()")) {
                                String message2 = message.replaceAll(finalReg, Dictionary.BANNED_REPLACE);
                                if (!message2.equals(message)) {
                                    Messenger.ruleNotify(Dictionary.NOTIFY_REPLACE, player, msg);
                                    message = message2;
                                }
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
                        recipient.sendMessage(Dictionary.formatMsg(Dictionary.FORMAT_PRIVATE_MESSAGE, "SERVER", server, "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));
                    }
                    sender.sendMessage(Dictionary.formatMsg(Dictionary.FORMAT_PRIVATE_MESSAGE, "SERVER", recipient.getServer().getInfo().getName(), "SENDER", sender.getName(), "RECIPIENT", recipient.getName(), "MESSAGE", message));
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
