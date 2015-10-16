package fr.mmarie.core.jira;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import fr.mmarie.api.jira.Comment;
import fr.mmarie.api.jira.Transition;
import fr.mmarie.api.jira.input.TransitionInput;
import fr.mmarie.api.jira.response.CommentResponse;
import fr.mmarie.api.jira.response.TransitionResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import retrofit.Callback;
import retrofit.JacksonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.mmarie.utils.Common.sanitizeURL;

@Slf4j
public class JiraService {
    public static final String TRANSITION_HOLDER = "{transition}";

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
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
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

    public TransitionResponse getTransitionsOfIssue(String issue) {
        return jiraEndPoints.getTransitionsOfIssue(issue).toBlocking().firstOrDefault(null);
    }

    public boolean transitionOnIssue(String issue, TransitionInput transitionInput) {
        try {
            Response<Void> response = jiraEndPoints.transitionsOnIssue(issue, transitionInput).execute();
            if(response.isSuccess()) {
                log.info("Transition {} has been made on {}", transitionInput, issue);
                return true;
            } else {
                log.error("Bad response received <" + response.code() + ">, "
                                + "unable to make the transition <{}> on <{}>, received message : " + response.message(),
                        issue,
                        transitionInput);

                try {
                    log.error("Response body received : {}",
                            response.errorBody().string());
                } catch (IOException e) {
                    log.error("Unable to read response body", e);
                }
            }
        } catch (IOException e) {
            log.error("Unable to perform transition <{}> on <{}>", transitionInput, issue);
        }

        return false;
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

    /**
     * Extracts all transitions from given {@code message}.
     *
     * @param message Commit message
     * @return Matching [ISSUE, TRANSITION]
     */
    public Map<String, String> extractMatchingTransitionsFromMessage(String message) {
        Map<String, String> matchingTransition = Maps.newHashMap();

        List<String> issues = extractIssuesFromMessage(message);

        issues.forEach(issue -> {
            Optional<String> optionalTransition = extractMatchingTransitionsFromMessage(message, issue);
            if(optionalTransition.isPresent()) {
                matchingTransition.put(issue, optionalTransition.get());
            }
        });

        return matchingTransition;
    }

    /**
     * Extracts first matching transition from given {@code message} related to the given issue.
     *
     * @param message Commit message
     * @return Matching transition if it exists
     */
    public Optional<String> extractMatchingTransitionsFromMessage(String message, String issue) {
        List<TransitionConfiguration> transitions = jiraConfiguration.getTransitions();

        for (TransitionConfiguration transition : transitions) {
            for (String keyword : transition.getKeywords()) {
                String regex = keyword.toLowerCase() + " #" + issue.toLowerCase();
                Matcher matcher = Pattern.compile(regex).matcher(message.toLowerCase());

                if(matcher.find()) {
                    return Optional.of(transition.getName());
                }
            }
        }

        return Optional.empty();
    }

    public boolean performTransition(String message, String issue, String comment) {
        Optional<String> optionalTransitionName = extractMatchingTransitionsFromMessage(message, issue);

        if(optionalTransitionName.isPresent()) {
            String transitionName = optionalTransitionName.get();
            Optional<Transition> optionalTransition = getTransition(issue, transitionName);

            if(optionalTransition.isPresent()) {
                log.info("Performing transition <{}> for issue <{}>", transitionName, issue);
                TransitionInput.CommentWrapper commentWrapper =
                        new TransitionInput.CommentWrapper(new Comment(comment.replace(TRANSITION_HOLDER, transitionName)));

                final TransitionInput transitionInput = new TransitionInput(
                        new TransitionInput.Update(ImmutableList.of(commentWrapper)),
                        optionalTransition.get()
                );

                return transitionOnIssue(issue, transitionInput);
            } else {
                log.warn("Transaction <{}> does not exists, issue <{}> can not be edited",
                        transitionName,
                        issue);
            }
        }

        return false;
    }

    public boolean isExistingIssue(String issue) {
        try {
            return (getIssue(issue).code() == 200);
        } catch (Exception e) {
            log.error("Unable to get issue <{}>", issue, e);
            return false;
        }
    }

    public Optional<Transition> getTransition(String issue, String name) {
        return getTransitionsOfIssue(issue)
                .getTransitions()
                .stream()
                .filter(transition -> transition.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public boolean isExistingTransition(String issue, String name) {
        return getTransition(issue, name).isPresent();
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
