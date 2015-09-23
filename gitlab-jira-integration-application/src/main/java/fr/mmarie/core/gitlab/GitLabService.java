package fr.mmarie.core.gitlab;

import com.google.common.collect.Lists;
import com.squareup.okhttp.OkHttpClient;
import fr.mmarie.api.gitlab.User;
import lombok.NonNull;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitLabService {

    /**
     * Matches issues with name like : #TEST-1, does not support special characters in
     * project name, #TEST-TEST-1 won't match.
     */
    public static final Pattern ISSUE_PATTERN = Pattern.compile("#\\s*(\\w+-\\d+)");

    private final GitLabEndPoints gitLabEndPoints;

    private final GitLabConfiguration gitLabConfiguration;

    public GitLabService(@NonNull GitLabConfiguration gitLabConfiguration) {
        this.gitLabConfiguration = gitLabConfiguration;

        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setReadTimeout(10, TimeUnit.SECONDS);
        httpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        this.gitLabEndPoints = new Retrofit.Builder()
                .baseUrl(gitLabConfiguration.getUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
                .create(GitLabEndPoints.class);
    }

    public Response<User> getUser(Long id) throws IOException {
        return gitLabEndPoints.getUser(id, gitLabConfiguration.getPrivateToken()).execute();
    }

    public Response<User> getLoggedUser() throws IOException {
        return gitLabEndPoints.getLoggedUser(gitLabConfiguration.getPrivateToken()).execute();
    }

    public String getUserUrl(String username) {
        String baseUrl = gitLabConfiguration.getUrl();
        if(!baseUrl.endsWith("/")) {
            baseUrl = baseUrl+"/";
        }
        return baseUrl.concat("u/"+username);
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
