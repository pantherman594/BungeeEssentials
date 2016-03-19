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

package com.pantherman594.gssentials.regex;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rule {
    private Pattern pattern = null;
    private Handle handle = null;
    private String replacement;
    private String command;
    private List<String> matches;

    /**
     * Create a new filter rule.
     */
    private Rule() {
        matches = Lists.newArrayList();
    }

    /**
     * Deserializes rules from the config for use.
     *
     * @param serialized Unformatted rule.
     * @return A deserialized Rule.
     */
    static Rule deserialize(Map<String, String> serialized) {
        Preconditions.checkNotNull(serialized);
        Preconditions.checkArgument(!serialized.isEmpty());
        Preconditions.checkNotNull(serialized.get("pattern"), "invalid pattern");
        Preconditions.checkNotNull(serialized.get("handle"), "invalid handler");

        Rule rule = new Rule();
        rule.pattern(String.valueOf(serialized.get("pattern")));
        rule.handle(Handle.valueOf(String.valueOf(serialized.get("handle")).toUpperCase()));
        if (serialized.get("replacement") != null) {
            rule.replacement = serialized.get("replacement");
        }
        if (serialized.get("command") != null) {
            rule.command = serialized.get("command");
        }
        return rule;
    }

    private Rule pattern(String pattern) {
        return pattern(pattern, false);
    }

    private Rule pattern(String pattern, boolean sensitive) {
        if (sensitive) {
            this.pattern = Pattern.compile(pattern);
        } else {
            this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        }
        return this;
    }

    private Rule handle(Handle handle) {
        this.handle = handle;
        return this;
    }

    public Handle getHandle() {
        return this.handle;
    }

    /**
     * @return Return the replacement if correct handler.
     */
    public String getReplacement() {
        if (handle != Handle.REPLACE) {
            return null;
        }
        return replacement;
    }

    /**
     * @return Return the command to run if correct handler.
     */
    public String getCommand() {
        if (handle != Handle.COMMAND) {
            return null;
        }
        return command;
    }

    /**
     * Check whether strings match the pattern, returns whether
     * the match is found and records it in the matches list.
     *
     * @param input The string to check for matches.
     * @return Whether a match for the string is found.
     */
    boolean matches(String input) {
        Preconditions.checkNotNull(input);
        matches.clear();
        Matcher matcher = pattern.matcher(input);
        boolean found = false;
        while (matcher.find()) {
            if (!found) {
                found = true;
            }
            matches.add(input.substring(matcher.start(), matcher.end()));
        }
        return found;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public List<String> getMatches() {
        return matches;
    }
}
