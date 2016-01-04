/*
 * BungeeEssentials: Full customization of a few necessary features for your server!
 * Copyright (C) 2015  David Shen (PantherMan594)
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

import com.pantherman594.gssentials.BungeeEssentials;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class RuleManager {
    private List<Rule> rules = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public RuleManager() {
        rules.clear();
        List<Map<String, String>> section = (List<Map<String, String>>) BungeeEssentials.getInstance().getMessages().getList("rules");
        for (Map<String, String> map : section) {
            Rule rule = Rule.deserialize(map);
            if (rule != null) {
                rules.add(rule);
            }
        }
        if (rules.size() > 0) {
            BungeeEssentials.getInstance().getLogger().log(Level.INFO, "Loaded {0} rules from config", rules.size());
        }
    }

    public List<MatchResult> matches(String input) {
        List<MatchResult> results = new ArrayList<>();
        Boolean contains = false;
        for (Rule rule : rules) {
            if (rule.matches(input)) {
                results.add(new MatchResult(true, rule));
                contains = true;
            } else {
                if (input.contains(" ")) {
                    for (String string : input.split(" ")) {
                        if (rule.matches(string)) {
                            results.add(new MatchResult(true, rule));
                            contains = true;
                        }
                    }
                }
            }
        }
        if (!contains) {
            results.add(new MatchResult(false, null));
        }
        return results;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public class MatchResult {
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
