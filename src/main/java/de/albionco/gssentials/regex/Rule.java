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

package de.albionco.gssentials.regex;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Connor Harries on 24/01/2015.
 *
 * @author Connor Spencer Harries
 */
public class Rule {
    private Pattern pattern = null;
    private Handle handle = null;
    private String replacement;
    private List<String> matches;

    private Rule() {
        matches = Lists.newArrayList();
    }

    public static Rule deserialize(Map<String, String> serialized) {
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
        return rule;
    }

    public Rule pattern(String pattern) {
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

    public String getReplacement() {
        if (handle != Handle.REPLACE) {
            return null;
        }
        return replacement;
    }

    public boolean matches(String input) {
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
