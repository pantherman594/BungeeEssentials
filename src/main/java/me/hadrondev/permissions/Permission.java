package me.hadrondev.permissions;

import net.md_5.bungee.api.CommandSender;

/**
 * Created by Connor Harries on 17/10/2014.
 */
public enum Permission {
    ADMIN_SEND("gssentials.admin.send"),
    ADMIN_SENDALL("gssentials.admin.sendall"),
    ADMIN_DISPATCH("gssentials.admin.dispatch"),
    ADMIN_ALERT("gssentials.admin.alert"),
    SLAP("gssentials.slap"),
    MESSAGE("gseentials.message");

    private final String node;

    private Permission(String node) {
        this.node = node;
    }

    public static boolean has(CommandSender sender, Permission permission) {
        return sender.hasPermission(permission.toString());
    }

    @Override
    public String toString() {
        return node;
    }
}
