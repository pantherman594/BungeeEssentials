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

import com.google.common.base.Preconditions;

import java.util.Map;

/**
 * Created by David on 5/9/2015.
 *
 * @author David Shen
 */
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
