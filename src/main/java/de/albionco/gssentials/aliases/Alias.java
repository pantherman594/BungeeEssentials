package de.albionco.gssentials.aliases;

import com.google.common.base.Preconditions;

import java.util.Map;

public class Alias {
    private String alias = null;
    private String[] commands = null;

    public static Alias deserialize(Map<String, String> serialized) {
        Preconditions.checkNotNull(serialized);
        Preconditions.checkArgument(!serialized.isEmpty());
        Preconditions.checkNotNull(serialized.get("alias"), "invalid alias");
        Preconditions.checkNotNull(serialized.get("commands"), "invalid commands");

        Alias alias = new Alias();
        alias.alias(String.valueOf(serialized.get("alias")));
        alias.commands(String.valueOf(serialized.get("commands")));
        return alias;
    }

    private Alias alias(String alias) {
        this.alias = alias;
        return this;
    }

    private Alias commands(String commands) {
        commands = commands.replace("[", "").replace("]", "");
        String[] newCmds;
        if (!commands.equals("")) {
            newCmds = commands.split(", ");
            this.commands = newCmds;
        } else {
            this.commands = null;
        }
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public String[] getCommands() {
        return commands;
    }
}
