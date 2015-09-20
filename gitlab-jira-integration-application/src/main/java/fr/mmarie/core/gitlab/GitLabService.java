package fr.mmarie.core.gitlab;

import com.google.common.collect.Lists;
import com.squareup.okhttp.OkHttpClient;
import lombok.NonNull;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitLabService {

    /**
     * Matches issues with name like : #TEST-1, does not support special characters in
     * project name, #TEST-TEST-1 won't match.
     */
    public static final Pattern ISSUE_PATTERN = Pattern.compile("#\\s*(\\w+-\\d+)");

    private final GitLabEndPoints gitLabEndPoints;

    private final String privateToken;

    public GitLabService(@NonNull GitLabConfiguration gitLabConfiguration) {
        this.privateToken = gitLabConfiguration.getPrivateToken();
        this.gitLabEndPoints = new Retrofit.Builder()
                .baseUrl(gitLabConfiguration.getUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GitLabEndPoints.class);
    }

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
