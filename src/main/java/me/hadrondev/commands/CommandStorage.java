package me.hadrondev.commands;

import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Connor Harries on 17/10/2014.
 */
public enum CommandStorage {
    ALERT(new Alert("alert")),
    DISPATCH(new Dispatch("dispatch")),
    FIND(new Find("find")),
    LIST(new ServerList("glist")),
    MESSAGE(new Message()),
    REPLY(new Reply("r")),
    SEND(new Send("send")),
    SEND_ALL(new SendAll("sendall")),
    SLAP(new Slap());

    private final Command command;

    private CommandStorage(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

}
