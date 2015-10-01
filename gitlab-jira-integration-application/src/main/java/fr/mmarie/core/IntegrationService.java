package fr.mmarie.core;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
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
        if(Strings.isNullOrEmpty(user.getUsername())) {
            return String.format("%s mentioned this issue in [a commit of %s|%s] \r\n "
                            + "*Commit message* : %s",
                    user.getName(), repositoryName, commit.getUrl(), commit.getMessage());
        } else {
            return String.format("[%s|%s] mentioned this issue in [a commit of %s|%s] \r\n"
                            + "*Commit message* : %s",
                    user.getName(), gitLabService.getUserUrl(user.getUsername()), repositoryName, commit.getUrl(), commit.getMessage());
        }
    }

    public void commentIssues(Event event, Commit commit, List<String> issues) throws IOException {
        Preconditions.checkNotNull(issues, "issues array can not be null");
        Preconditions.checkNotNull(commit, "commit can not be null");

        if(issues.size() > 0) {
            try {
                Response<User> userResponse = gitLabService.getUser(event.getUserId());
                Preconditions.checkArgument(userResponse.code() == 200);

                User user = userResponse.body();

                issues.forEach(issue -> commentIssue(event.getRepository().getName(), commit, user, issue));
            } catch (Exception e) {
                log.error("Unable to get gitlab user with id <{}>, comment issue with pusher username <{}>",
                        event.getUserId(),
                        event.getUserName());
                User user = new User(event.getUserId(), null, event.getUserName());
                issues.forEach(issue -> commentIssue(event.getRepository().getName(), commit, user, issue));
            }

            log.info("<{}> mentioned these issues <{}> in <{}>",
                    event.getUserName(),
                    issues.size(),
                    commit);
        }
    }

    public void commentIssue(String repositoryName, Commit commit, User user, String issue) {
        if(jiraService.isExistingIssue(issue)) {
            try {
                log.info("Comment issue <{}> for commit <{}>", issue, commit);
                jiraService.commentIssue(issue,
                        new Comment(buildComment(user,
                                repositoryName,
                                commit)));
            } catch (IOException e) {
                log.error("Unable to comment issue <{}>", issue, e);
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
