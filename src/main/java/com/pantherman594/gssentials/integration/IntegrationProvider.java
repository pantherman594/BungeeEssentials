/*
 * BungeeEssentials: Full customization of a few necessary features for your server!
 * Copyright (C) 2016 David Shen (PantherMan594)
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

package com.pantherman594.gssentials.integration;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class IntegrationProvider {
    private static Map<String, Class<? extends IntegrationProvider>> providers = new HashMap<>();
    private static Map<Class<? extends IntegrationProvider>, IntegrationProvider> instances = new HashMap<>();

    static {
        /*
         * If we create new instances of the provider classes and the plugin
         * doesn't exist or hasn't been registered then exceptions are thrown
         */
        providers.put("BungeeAdminTools", AdminToolsProvider.class);
        providers.put("BungeeSuite", BungeeSuiteProvider.class);
    }

    public static IntegrationProvider get(String name) {
        Class<? extends IntegrationProvider> clazz = providers.get(name);
        if (instances.get(clazz) == null) {
            try {
                instances.put(clazz, clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                try {
                    Constructor<? extends IntegrationProvider> providerConstructor = clazz.getDeclaredConstructor();
                    if (!providerConstructor.isAccessible()) {
                        providerConstructor.setAccessible(true);
                    }
                    IntegrationProvider provider = providerConstructor.newInstance();
                    instances.put(clazz, provider);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e1) {
                    // ignored, nothing we can really do at this point
                }
            }
        }
        return instances.get(clazz);
    }

    public static Set<String> getPlugins() {
        return providers.keySet();
    }
    
    public abstract boolean isMuted(ProxiedPlayer player);

    public abstract boolean isBanned(ProxiedPlayer player);

    public abstract boolean isEnabled();
    public abstract String getName();
}
