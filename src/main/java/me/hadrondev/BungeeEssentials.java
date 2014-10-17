package me.hadrondev;

import me.hadrondev.commands.CommandStorage;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeEssentials extends Plugin {

    public static BungeeEssentials me;

    @Override
    public void onEnable() {
        me = this;
        getProxy().getPluginManager().registerListener(this, new BungeeEssentialsListener());

        for(CommandStorage cmd : CommandStorage.values()) {
            getProxy().getPluginManager().registerCommand(this, cmd.getCommand());
        }
    }

}
