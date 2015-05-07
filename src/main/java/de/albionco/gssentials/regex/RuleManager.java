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

import de.albionco.gssentials.BungeeEssentials;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by Connor Harries on 24/01/2015.
 *
 * @author Connor Spencer Harries
 */
public class RuleManager {
    private static List<Rule> rules = new ArrayList<>();

    public static boolean register(Rule rule) {
        if (!rules.contains(rule)) {
            rules.add(rule);
            return true;
        }
        return false;
    }

    public static MatchResult matches(String input) {
        for (Rule rule : rules) {
            if (rule.matches(input)) {
                return new MatchResult(true, rule);
            } else {
                if (input.contains(" ")) {
                    for (String string : input.split(" ")) {
                        if (rule.matches(string)) {
                            return new MatchResult(true, rule);
                        }
                    }
                }
            }
        }
        return new MatchResult(false, null);
    }

    @SuppressWarnings("unchecked")
    public static boolean load() {
        List<Map<String, String>> section = (List<Map<String, String>>) BungeeEssentials.getInstance().getConfig().getList("rules");
        int success = 0;
        for (Map<String, String> map : section) {
            Rule rule = Rule.deserialize(map);
            if (rule != null) {
                if (register(rule)) {
                    success++;
                }
            }
        }
        if (success > 0) {
            BungeeEssentials.getInstance().getLogger().log(Level.INFO, "Loaded {0} rules from config", success);
        }
        return true;
    }

    public static class MatchResult {
        private final boolean success;
        private final Rule rule;

        public MatchResult(boolean success, Rule rule) {
            this.success = success;
            this.rule = rule;
        }

        public boolean matched() {
            return success;
        }

        public Rule getRule() {
            return rule;
        }
    }
}
