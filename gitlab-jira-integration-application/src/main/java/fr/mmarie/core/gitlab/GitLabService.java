package fr.mmarie.core.gitlab;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitLabService {

    /**
     * Matches issues with name like : #TEST-1, does not support special characters in
     * project name, #TEST-TEST-1 won't match.
     */
    public static final Pattern ISSUE_PATTERN = Pattern.compile("#\\s*(\\w+-\\d+)");

    /**
     * Extracts issues names from given {@code message}.
     *
     * @param message Commit message
     * @return Matching issues name
     */
    public List<String> extractIssuesFromMessage(String message) {
        List<String> issues = Lists.newArrayList();

        Matcher matcher = ISSUE_PATTERN.matcher(message);

        while (matcher.find()) {
            issues.add(matcher.group(1));
        }

        return issues;
    }
}
