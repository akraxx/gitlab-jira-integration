package fr.mmarie.core.jira;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import fr.mmarie.api.jira.Comment;
import fr.mmarie.api.jira.response.CommentResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.mmarie.utils.Common.sanitizeURL;

@Slf4j
public class JiraService {

    /**
     * Matches issues with name like : #TEST-1, does not support special characters in
     * project name, #TEST-TEST-1 won't match.
     */
    public static final Pattern ISSUE_PATTERN = Pattern.compile("#\\s*(\\w+-\\d+)");

    private final JiraConfiguration jiraConfiguration;

    private final JiraEndPoints jiraEndPoints;

    @Inject
    public JiraService(@NonNull JiraConfiguration jiraConfiguration) {
        this.jiraConfiguration = jiraConfiguration;

        OkHttpClient httpClient = new OkHttpClient();
        httpClient.interceptors().add(chain -> {
            String credentials = jiraConfiguration.getUsername() + ":" + jiraConfiguration.getPassword();
            String encodedHeader = "Basic " + new String(Base64.getEncoder().encode(credentials.getBytes()));

            Request requestWithAuthorization = chain.request().newBuilder().addHeader("Authorization", encodedHeader).build();
            return chain.proceed(requestWithAuthorization);
        });

        this.jiraEndPoints = new Retrofit.Builder()
                .baseUrl(sanitizeURL(jiraConfiguration.getUrl()))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
                .create(JiraEndPoints.class);
    }

    public Response<Object> getIssue(String issue) throws IOException {
        return jiraEndPoints.getIssue(issue).execute();
    }

    public Response<CommentResponse> getCommentsOfIssue(String issue) throws IOException {
        return jiraEndPoints.getCommentsOfIssue(issue).execute();
    }

    public Response<Comment> commentIssue(String issue, Comment comment) throws IOException {
        return jiraEndPoints.commentIssue(issue, comment).execute();
    }

    public Response<Map<String, Object>> serverInfo() throws IOException {
        return jiraEndPoints.serverInfo().execute();
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

    public boolean isExistingIssue(String issue) {
        try {
            return (getIssue(issue).code() == 200);
        } catch (Exception e) {
            log.error("Unable to get issue <{}>", issue, e);
            return false;
        }
    }

    public boolean isIssueAlreadyCommented(String issue, String commitId) {
        try {
            List<Comment> comments = getCommentsOfIssue(issue).body().getComments();
            return comments.stream().anyMatch(comment -> comment.getBody().contains(commitId));
        } catch (IOException e) {
            return true;
        }
    }
}
