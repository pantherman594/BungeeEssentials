/*
 * Copyright (c) 2015 Connor Spencer Harries
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.albionco.gssentials.aliases;

import de.albionco.gssentials.BungeeEssentials;
import net.md_5.bungee.api.ProxyServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by David on 5/9/2015.
 *
 * @author David Shen
 */
public class AliasManager {
    public static List<Alias> aliases = new ArrayList<>();

    public static boolean register(Alias alias) {
        if (!aliases.contains(alias)) {
            aliases.add(alias);
            ProxyServer.getInstance().getPluginManager().registerCommand(BungeeEssentials.getInstance(), new LoadCmds(alias.getAlias(), alias.getCommands()));
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static boolean load() {
        aliases.clear();
        List<Map<String, String>> section = (List<Map<String, String>>) BungeeEssentials.getInstance().getConfig().getList("aliases");
        int success = 0;
        for (Map<String, String> map : section) {
            Alias alias = Alias.deserialize(map);
            if (alias != null) {
                if (register(alias)) {
                    success++;
                }
            }
        }
        if (success > 0) {
            BungeeEssentials.getInstance().getLogger().log(Level.INFO, "Loaded {0} aliases from config", success);
        }
        return true;
    }
}
