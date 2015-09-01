package de.albionco.gssentials.regex;

import de.albionco.gssentials.BungeeEssentials;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class RuleManager {
    private static List<Rule> rules = new ArrayList<>();

    public static boolean register(Rule rule) {
        if (!rules.contains(rule)) {
            rules.add(rule);
            return true;
        }
        return false;
    }

    public static List<MatchResult> matches(String input) {
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

    @SuppressWarnings("unchecked")
    public static boolean load() {
        rules.clear();
        List<Map<String, String>> section = (List<Map<String, String>>) BungeeEssentials.getInstance().getMessages().getList("rules");
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
