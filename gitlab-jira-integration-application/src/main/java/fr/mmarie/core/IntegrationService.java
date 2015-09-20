package fr.mmarie.core;

import com.google.common.base.Preconditions;
import fr.mmarie.api.gitlab.Commit;
import fr.mmarie.api.gitlab.Event;
import fr.mmarie.api.gitlab.User;
import fr.mmarie.api.jira.Comment;
import fr.mmarie.core.gitlab.GitLabService;
import fr.mmarie.core.jira.JiraService;
import lombok.extern.slf4j.Slf4j;
import retrofit.Response;

import java.io.IOException;
import java.util.List;

@Slf4j
public class IntegrationService {
    private final GitLabService gitLabService;
    private final JiraService jiraService;

    public IntegrationService(GitLabService gitLabService, JiraService jiraService) {
        this.gitLabService = gitLabService;
        this.jiraService = jiraService;
    }

    public String buildComment(User user, String repositoryName, Commit commit) {
        return String.format("[%s|%s] mentioned this issue in [a commit of %s|%s]",
                user.getName(), gitLabService.getUserUrl(user.getUsername()), repositoryName, commit.getUrl());
    }

    public void commentIssues(Event event, Commit commit, List<String> issues) throws IOException {
        Preconditions.checkNotNull(issues, "issues array can not be null");

        if(issues.size() > 0) {
            User user;
            try {
                Response<User> userResponse = gitLabService.getUser(event.getUserId());
                Preconditions.checkArgument(userResponse.code() == 200);
                user = userResponse.body();
            } catch (Exception e) {
                log.error("Unable to get gitlab user with id <{}>", event.getUserId());
                throw e;
            }

            log.info("<{}> mentioned these issues <{}> in <{}>",
                    user,
                    issues.size(),
                    commit);

            // Comment each issue
            issues.forEach(issue -> commentIssue(event, commit, user, issue));
        }
    }

    private void commentIssue(Event event, Commit commit, User user, String issue) {
        if(jiraService.isExistingIssue(issue)) {
            try {
                log.info("Comment issue <{}> for commit <{}>", issue, commit);
                jiraService.commentIssue(issue,
                        new Comment(buildComment(user,
                                event.getRepository().getName(),
                                commit)));
            } catch (IOException e) {
                log.error("Unable to comment issue <{}>", issue);
            }
        } else {
            log.warn("Issue <{}> has been mentioned, but does not exists", issue);
        }
    }

    public void performPushEvent(Event event) {
        Preconditions.checkNotNull(event.getCommits(), "commits array can not be null");
        // For each commit, extract jira issues
        event.getCommits().forEach(commit -> {
            try {
                commentIssues(event, commit, gitLabService.extractIssuesFromMessage(commit.getMessage()));
            } catch (IOException e) {
                log.error("Unable to comment issues", e);
            }
        });
    }

}
