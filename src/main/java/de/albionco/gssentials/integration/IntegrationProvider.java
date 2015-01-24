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

package de.albionco.gssentials.integration;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Connor Harries on 24/01/2015.
 *
 * @author Connor Spencer Harries
 */
public abstract class IntegrationProvider {
    private static HashMap<String, Class<? extends IntegrationProvider>> providers = new HashMap<>();
    private static HashMap<Class<? extends IntegrationProvider>, IntegrationProvider> instances = new HashMap<>();

    static {
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

    public abstract String getName();
}
