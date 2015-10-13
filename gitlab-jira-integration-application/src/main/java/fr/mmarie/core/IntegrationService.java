package fr.mmarie.core;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import fr.mmarie.api.gitlab.Commit;
import fr.mmarie.api.gitlab.Event;
import fr.mmarie.api.gitlab.User;
import fr.mmarie.api.jira.Comment;
import fr.mmarie.core.gitlab.GitLabService;
import fr.mmarie.core.jira.JiraService;
import lombok.extern.slf4j.Slf4j;
import retrofit.Response;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

@Slf4j
public class IntegrationService {
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    private final GitLabService gitLabService;
    private final JiraService jiraService;

    @Inject
    public IntegrationService(GitLabService gitLabService, JiraService jiraService) {
        this.gitLabService = gitLabService;
        this.jiraService = jiraService;
    }

    public String getDateAsString(Date date) {
        if (date == null) {
            return "unknown date of commit";
        } else {
            return DATE_FORMAT.format(date);
        }
    }

    public String buildComment(User user, String repositoryName, Commit commit) {
        if(Strings.isNullOrEmpty(user.getUsername())) {
            return String.format("%s mentioned this issue in [a commit of %s|%s] \r\n "
                            + "*%s* : %s",
                    user.getName(), repositoryName, commit.getUrl(),
                    getDateAsString(commit.getTimestamp()), commit.getMessage());
        } else {
            return String.format("[%s|%s] mentioned this issue in [a commit of %s|%s] \r\n"
                            + "*%s* : %s",
                    user.getName(), gitLabService.getUserUrl(user.getUsername()), repositoryName, commit.getUrl(),
                    getDateAsString(commit.getTimestamp()), commit.getMessage());
        }
    }

    public String buildCommentForTransition(User user, String repositoryName, Commit commit) {
        if(Strings.isNullOrEmpty(user.getUsername())) {
            return String.format("%s changed status of this issue in [a commit of %s|%s] to *%s* \r\n\r\n "
                            + "*%s* : %s",
                    user.getName(), repositoryName, commit.getUrl(),
                    JiraService.TRANSITION_HOLDER,
                    getDateAsString(commit.getTimestamp()), commit.getMessage());
        } else {
            return String.format("[%s|%s] changed status of this issue in [a commit of %s|%s] to *%s* \r\n\r\n"
                            + "*%s* : %s",
                    user.getName(), gitLabService.getUserUrl(user.getUsername()), repositoryName, commit.getUrl(),
                    JiraService.TRANSITION_HOLDER,
                    getDateAsString(commit.getTimestamp()), commit.getMessage());
        }
    }

    public void commentIssue(String repositoryName, User user, Collection<Commit> commits, String issue) {
        if(jiraService.isExistingIssue(issue)) {
            commits.forEach(commit -> {
                if(!jiraService.isIssueAlreadyCommented(issue, commit.getId())) {
                    try {
                        log.info("Comment issue <{}> for commit <{}>", issue, commit);
                        jiraService.commentIssue(issue,
                                new Comment(buildComment(user,
                                        repositoryName,
                                        commit)));
                        jiraService.performTransition(commit.getMessage(), issue,
                                buildCommentForTransition(user,
                                        repositoryName,
                                        commit));
                    } catch (IOException e) {
                        log.error("Unable to comment issue <{}>", issue, e);
                    }
                } else {
                    log.warn("Issue <{}> has already been commented for commit <{}>",
                            issue,
                            commit.getId());
                }
            });
        } else {
            log.warn("Issue <{}> has been mentioned, but does not exists", issue);
        }
    }

    public void performPushEvent(Event event) {
        Preconditions.checkNotNull(event.getCommits(), "commits array can not be null");
        // For each commit, extract jira issues
        Multimap<String, Commit> jiraIssues = ArrayListMultimap.create();
        event.getCommits().forEach(commit ->
                jiraService.extractIssuesFromMessage(commit.getMessage())
                        .forEach(issue -> jiraIssues.put(issue, commit)));

        if(jiraIssues.size() > 0) {
            User user = getUser(event);

            jiraIssues.asMap().forEach((issue, commits) -> commentIssue(event.getRepository().getName(), user, commits, issue));
        }
    }

    public User getUser(Event event) {
        try {
            Response<User> userResponse = gitLabService.getUser(event.getUserId());
            Preconditions.checkArgument(userResponse.code() == 200);

            return userResponse.body();
        } catch (Exception e) {
            log.error("Unable to get gitlab user with id <{}>, comment issue with pusher username <{}>",
                    event.getUserId(),
                    event.getUserName());
            return new User(event.getUserId(), null, event.getUserName());
        }
    }
}
