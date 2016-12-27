package com.pantherman594.gssentials.command.general;

import com.google.common.collect.ImmutableSet;
import com.pantherman594.gssentials.BungeeEssentials;
import com.pantherman594.gssentials.Dictionary;
import com.pantherman594.gssentials.Messenger;
import com.pantherman594.gssentials.Permissions;
import com.pantherman594.gssentials.command.BECommand;
import com.pantherman594.gssentials.database.MsgGroups;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by david on 8/31.
 */
@SuppressWarnings("unused")
public class MsgGroupCommand extends BECommand implements TabExecutor {

    private MsgGroups msgGroups;

    public MsgGroupCommand() {
        super("msggroup", "");
        msgGroups = BungeeEssentials.getInstance().getMsgGroups();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("admin") && Permissions.hasPerm(sender, Permissions.Admin.MSGGROUP)) {
            String uuid = "CONSOLE";
            if (sender instanceof ProxiedPlayer) {
                uuid = ((ProxiedPlayer) sender).getUniqueId().toString();
            }

            if (args.length > 1) {
                if (args.length == 2 && args[1].equalsIgnoreCase("listgroups")) {
                    sender.sendMessage(Dictionary.format(Dictionary.MGA_LIST_GROUPS_HEADER));
                    List<Object> groups = msgGroups.listAllData("groupname");
                    if (groups != null) {
                        for (Object o : groups) {
                            String name = (String) o;
                            sender.sendMessage(Dictionary.format(Dictionary.MGA_LIST_GROUPS_BODY, "NAME", name, "OWNER", msgGroups.getOwner(name), "MEMBERS", Dictionary.combine(", ", msgGroups.getMembers(name))));
                        }
                    } else {
                        sender.sendMessage(Dictionary.format("None found."));
                    }
                } else if (args.length == 3) {
                    String name = args[2].toLowerCase();
                    switch (args[1]) {
                        case "join":
                            if (Permissions.hasPerm(sender, Permissions.Admin.MG_FORCE_JOIN, Permissions.Admin.MG_ALL)) {
                                if (msgGroups.createDataNotExist(name)) {
                                    if (msgGroups.getMembers(name).contains(uuid)) {
                                        sender.sendMessage(Dictionary.format(Dictionary.MG_ERROR_ALREADY_IN_GROUP, "NAME", name));
                                    } else {
                                        msgGroups.addMember(name, uuid);
                                        sender.sendMessage(Dictionary.format(Dictionary.MG_JOIN, "NAME", name));
                                    }
                                } else {
                                    sender.sendMessage(Dictionary.format(Dictionary.MG_ERROR_NOT_EXIST, "NAME", name));
                                }
                            } else {
                                sender.sendMessage(ProxyServer.getInstance().getTranslation("no_permission"));
                            }
                            break;
                        case "disband":
                            if (Permissions.hasPerm(sender, Permissions.Admin.MG_DISBAND, Permissions.Admin.MG_ALL)) {
                                if (msgGroups.createDataNotExist(name)) {
                                    ProxyServer.getInstance().getPlayers().stream().filter(recipient -> msgGroups.getMembers(name).contains(recipient.getUniqueId().toString())).forEach(recipient -> recipient.sendMessage(Dictionary.format(Dictionary.MG_KICK_RECEIVE, "NAME", name)));
                                    msgGroups.remove(name);
                                    sender.sendMessage(Dictionary.format(Dictionary.MG_DISBAND, "NAME", name));
                                } else {
                                    sender.sendMessage(Dictionary.format(Dictionary.MG_ERROR_NOT_EXIST, "NAME", name));
                                }
                            } else {
                                sender.sendMessage(ProxyServer.getInstance().getTranslation("no_permission"));
                            }
                            break;
                        default:
                            helpMsg(sender);
                    }
                } else if (args.length == 4) {
                    switch (args[1]) {
                        case "join":
                            if (Permissions.hasPerm(sender, Permissions.Admin.MG_FORCE_JOIN, Permissions.Admin.MG_ALL)) {
                                String name = args[3].toLowerCase();
                                ProxiedPlayer recipient = ProxyServer.getInstance().getPlayer(args[2]);
                                if (recipient != null) {
                                    if (msgGroups.createDataNotExist(name)) {
                                        if (msgGroups.getMembers(name).contains(recipient.getUniqueId().toString())) {
                                            sender.sendMessage(Dictionary.format(ChatColor.GREEN + recipient.getName() + Dictionary.MG_ERROR_ALREADY_IN_GROUP, "NAME", name));
                                        } else {
                                            msgGroups.addMember(name, recipient.getUniqueId().toString());
                                            recipient.sendMessage(Dictionary.format(Dictionary.MG_JOIN, "NAME", name));
                                            sender.sendMessage(Dictionary.format(ChatColor.GREEN + recipient.getName() + Dictionary.MG_JOIN, "NAME", name));
                                        }
                                    } else {
                                        sender.sendMessage(Dictionary.format(Dictionary.MG_ERROR_NOT_EXIST, "NAME", name));
                                    }
                                } else {
                                    sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_NOT_FOUND));
                                }
                            } else {
                                sender.sendMessage(ProxyServer.getInstance().getTranslation("no_permission"));
                            }
                            break;
                        case "makeowner":
                            if (Permissions.hasPerm(sender, Permissions.Admin.MG_MAKE_OWNER, Permissions.Admin.MG_ALL)) {
                                String name = args[2].toLowerCase();
                                ProxiedPlayer recipient = ProxyServer.getInstance().getPlayer(args[3]);
                                if (msgGroups.createDataNotExist(name)) {
                                    if (recipient != null) {
                                        msgGroups.addMember(name, recipient.getUniqueId().toString());
                                        msgGroups.setOwner(name, recipient.getUniqueId().toString());
                                        ProxyServer.getInstance().getPlayers().stream().filter(p -> msgGroups.getMembers(name).contains(p.getUniqueId().toString()) || p == sender).forEach(p -> p.sendMessage(Dictionary.format(Dictionary.MGA_OWNER, "PLAYER", recipient.getName(), "NAME", name)));
                                    } else {
                                        sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_NOT_FOUND));
                                    }
                                } else {
                                    sender.sendMessage(Dictionary.format(Dictionary.MG_ERROR_NOT_EXIST, "NAME", name));
                                }
                            } else {
                                sender.sendMessage(ProxyServer.getInstance().getTranslation("no_permission"));
                            }
                            break;
                        case "kick":
                            if (Permissions.hasPerm(sender, Permissions.Admin.MG_KICK, Permissions.Admin.MG_ALL)) {
                                String name = args[3].toLowerCase();
                                String recipient = ProxyServer.getInstance().getPlayer(args[2]).getUniqueId().toString();
                                boolean online = false;

                                if (recipient == null) {
                                    recipient = (String) pD.getData("lastname", args[2], "uuid");
                                } else {
                                    online = true;
                                }

                                if (recipient == null) {
                                    sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_NOT_FOUND));
                                    break;
                                }

                                if (msgGroups.getMembers(name).contains(recipient)) {
                                    msgGroups.removeMember(name, recipient);
                                    if (online) {
                                        ProxyServer.getInstance().getPlayer(args[2]).sendMessage(Dictionary.format(Dictionary.MG_KICK_RECEIVE, "NAME", name));
                                    }
                                    sender.sendMessage(Dictionary.format(Dictionary.MG_KICK_SEND, "NAME", name, "PLAYER", args[2]));
                                } else {
                                    sender.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_NOT_FOUND));
                                }
                            } else {
                                sender.sendMessage(ProxyServer.getInstance().getTranslation("no_permission"));
                            }
                            break;
                        default:
                            helpMsg(sender);
                    }
                }
            } else {
                helpMsg(sender);
            }
        } else if (args.length > 0) {
            if (sender instanceof ProxiedPlayer) {
                ProxiedPlayer p = (ProxiedPlayer) sender;
                String uuid = p.getUniqueId().toString();
                if (Permissions.hasPerm(p, Permissions.General.MSGGROUP)) {
                    if (args.length == 2 && (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("leave"))) {
                        String name = args[1].toLowerCase();
                        switch (args[0].toLowerCase()) {
                            case "create":
                                if (Permissions.hasPerm(p, Permissions.General.MG_CREATE)) {
                                    if (name.length() < 3) {
                                        p.sendMessage(Dictionary.format(Dictionary.MG_ERROR_INVALID_NAME, "NAME", name));
                                        return;
                                    }
                                    for (char c : args[1].toCharArray()) {
                                        if (!Character.isLetter(c)) {
                                            p.sendMessage(Dictionary.format(Dictionary.MG_ERROR_INVALID_NAME, "NAME", name));
                                            return;
                                        }
                                    }
                                    if (msgGroups.createDataNotExist(name)) {
                                        sender.sendMessage(Dictionary.format(Dictionary.MG_ERROR_NAME_TAKEN, "NAME", name));
                                        return;
                                    }
                                    msgGroups.create(name);
                                    msgGroups.setOwner(name, uuid);
                                    p.sendMessage(Dictionary.format(Dictionary.MG_CREATE, "NAME", name));
                                } else {
                                    p.sendMessage(ProxyServer.getInstance().getTranslation("no_permission"));
                                }
                                break;
                            case "join":
                                if (msgGroups.createDataNotExist(name) && msgGroups.getMembers(name).contains(uuid)) {
                                    p.sendMessage(Dictionary.format(Dictionary.MG_ERROR_ALREADY_IN_GROUP, "NAME", name));
                                } else if (msgGroups.createDataNotExist(name) && msgGroups.getInvited(name).contains(uuid)) {
                                    msgGroups.removeInvited(name, uuid);
                                    msgGroups.addMember(name, uuid);
                                    p.sendMessage(Dictionary.format(Dictionary.MG_JOIN, "NAME", name));
                                } else {
                                    p.sendMessage(Dictionary.format(Dictionary.MG_ERROR_NOT_INVITED, "NAME", name));
                                }
                                break;
                            case "leave":
                                if (msgGroups.createDataNotExist(name) && msgGroups.getMembers(name).contains(uuid)) {
                                    if (msgGroups.getOwner(name).equals(uuid)) {
                                        msgGroups.remove(name);
                                        p.sendMessage(Dictionary.format(Dictionary.MG_DISBAND, "NAME", name));
                                    } else {
                                        msgGroups.removeMember(name, uuid);
                                        p.sendMessage(Dictionary.format(Dictionary.MG_LEAVE, "NAME", name));
                                    }
                                } else {
                                    p.sendMessage(Dictionary.format(Dictionary.MG_ERROR_NOT_IN_GROUP, "NAME", name));
                                }
                                break;
                        }
                    } else if (args.length == 3 && (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("kick"))) {
                        String name = args[2].toLowerCase();
                        if (msgGroups.createDataNotExist(name) && msgGroups.getOwner(name).equals(uuid)) {
                            ProxiedPlayer recipient = ProxyServer.getInstance().getPlayer(args[1]);
                            if (recipient != null) {
                                switch (args[0].toLowerCase()) {
                                    case "invite":
                                        msgGroups.addInvited(name, recipient.getUniqueId().toString());
                                        recipient.sendMessage(Dictionary.format(Dictionary.MG_INVITE_RECEIVE, "NAME", name));
                                        p.sendMessage(Dictionary.format(Dictionary.MG_INVITE_SEND, "NAME", name, "PLAYER", recipient.getName()));
                                        break;
                                    case "kick":
                                        if (msgGroups.getMembers(name).contains(recipient.getUniqueId().toString())) {
                                            msgGroups.removeMember(name, recipient.getUniqueId().toString());
                                            recipient.sendMessage(Dictionary.format(Dictionary.MG_KICK_RECEIVE, "NAME", name));
                                            p.sendMessage(Dictionary.format(Dictionary.MG_KICK_SEND, "NAME", name, "PLAYER", recipient.getName()));
                                        } else {
                                            p.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_NOT_FOUND));
                                        }
                                        break;
                                }
                            } else {
                                p.sendMessage(Dictionary.format(Dictionary.ERROR_PLAYER_NOT_FOUND));
                            }
                        } else {
                            p.sendMessage(Dictionary.format(Dictionary.MG_ERROR_NOT_IN_GROUP, "NAME", name));
                        }
                    } else if (args.length == 3 && args[0].equalsIgnoreCase("rename")) {
                        String oldName = args[1].toLowerCase();
                        String name = args[2].toLowerCase();
                        if (msgGroups.createDataNotExist(oldName) && msgGroups.getOwner(oldName).equals(uuid)) {
                            if (name.length() < 3) {
                                p.sendMessage(Dictionary.format(Dictionary.MG_ERROR_INVALID_NAME, "NAME", name));
                                return;
                            }
                            for (char c : args[1].toCharArray()) {
                                if (!Character.isLetter(c)) {
                                    p.sendMessage(Dictionary.format(Dictionary.MG_ERROR_INVALID_NAME, "NAME", name));
                                    return;
                                }
                            }
                            if (msgGroups.createDataNotExist(name)) {
                                sender.sendMessage(Dictionary.format(Dictionary.MG_ERROR_NAME_TAKEN, "NAME", name));
                                return;
                            }
                            msgGroups.setName(oldName, name);
                            for (String member : msgGroups.getMembers(name)) {
                                ProxiedPlayer memberP = ProxyServer.getInstance().getPlayer(UUID.fromString(member));
                                if (memberP != null) {
                                    memberP.sendMessage(Dictionary.format(Dictionary.MG_RENAME, "OLDNAME", oldName, "NAME", name));
                                }
                            }
                        } else {
                            p.sendMessage(Dictionary.format(Dictionary.MG_ERROR_NOT_IN_GROUP, "NAME", name));
                        }
                    } else if (args.length > 1) {
                        String name = args[0].toLowerCase();
                        if (msgGroups.createDataNotExist(name) && msgGroups.getMembers(name).contains(uuid)) {
                            Set<String> members = msgGroups.getMembers(name);
                            String messageS = BungeeEssentials.getInstance().getMessenger().filter(p, Dictionary.combine(0, " ", args), Messenger.ChatType.PRIVATE);
                            TextComponent msg = Dictionary.format(Dictionary.MG_FORMAT, "NAME", Dictionary.capitalizeFirst(name), "SENDER", p.getName(), "MESSAGE", messageS);
                            TextComponent spyMsg = Dictionary.format(Dictionary.SPY_MESSAGE, "SENDER", p.getName(), "RECIPIENT", ChatColor.BLUE + Dictionary.capitalizeFirst(name), "MESSAGE", messageS);

                            for (ProxiedPlayer recip : ProxyServer.getInstance().getPlayers()) {
                                if (members.contains(recip.getUniqueId().toString())) {
                                    recip.sendMessage(msg);
                                } else if (pD.isSpy(recip.getUniqueId().toString())) {
                                    recip.sendMessage(spyMsg);
                                }
                            }
                        } else {
                            p.sendMessage(Dictionary.format(Dictionary.MG_ERROR_NOT_IN_GROUP, "NAME", name));
                        }
                    } else {
                        helpMsg(p);
                    }
                } else {
                    p.sendMessage(ProxyServer.getInstance().getTranslation("no_permission"));
                }
            } else {
                sender.sendMessage(Dictionary.format("&cYou can't do that as console!"));
            }
        } else {
            helpMsg(sender);
        }
    }

    private void helpMsg(CommandSender sender) {
        if (Permissions.hasPerm(sender, Permissions.General.MSGGROUP)) {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP",
                    "\n  /" + getName() + " <group> <message>" +
                            "\n  /" + getName() + " <create|join|leave> <group>" +
                            "\n  /" + getName() + " <invite|kick> <username> <group>" +
                            "\n  /" + getName() + " rename <oldname> <group>"));
        }
        if (Permissions.hasPerm(sender, Permissions.Admin.MSGGROUP)) {
            sender.sendMessage(Dictionary.format(Dictionary.ERROR_INVALID_ARGUMENTS, "HELP",
                    "\n  /" + getName() + " admin listgroups" +
                            "\n  /" + getName() + " admin <disband> <group>" +
                            "\n  /" + getName() + " admin <makeowner> <group> <username>" +
                            "\n  /" + getName() + " admin <join|kick> [username] <group>"));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        //TODO: Tab completion for invite, maybe for join (show invited), disband (if have permission)
        return args.length == 1 ? tabPlayers(sender, args[0]) : ImmutableSet.of();
    }
}
