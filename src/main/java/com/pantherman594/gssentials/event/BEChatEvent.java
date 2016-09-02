package com.pantherman594.gssentials.event;

import com.pantherman594.gssentials.BungeeEssentials;
import com.pantherman594.gssentials.database.PlayerData;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

/**
 * Created by david on 9/01.
 */
@SuppressWarnings("WeakerAccess")
public abstract class BEChatEvent extends Event implements Cancellable {
    public PlayerData pD = BungeeEssentials.getInstance().getPlayerData();
    private String server;
    private String sender;
    private String msg;
    private boolean cancelled;

    /**
     * The Chat event.
     *
     * @param server The server that the chat sender is on.
     * @param sender The chat sender's name.
     * @param msgPre The message before formatting/filtering.
     */
    public BEChatEvent(String server, String sender, String msgPre) {
        this.server = server;
        this.sender = sender;
        this.msg = msgPre;
        execute();
    }

    public abstract void execute();

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public abstract String toString();
}
