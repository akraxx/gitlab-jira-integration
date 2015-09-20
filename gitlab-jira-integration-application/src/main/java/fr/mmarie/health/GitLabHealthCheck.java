package fr.mmarie.health;

import com.codahale.metrics.health.HealthCheck;
import fr.mmarie.core.gitlab.GitLabService;
import fr.mmarie.core.jira.JiraService;

import javax.ws.rs.core.Response;

public class GitLabHealthCheck extends HealthCheck {

    private final GitLabService gitLabService;

    public GitLabHealthCheck(GitLabService gitLabService) {
        this.gitLabService = gitLabService;
    }

    @Override
    protected Result check() throws Exception {
        int code = gitLabService.getLoggedUser().code();
        if(code == Response.Status.OK.getStatusCode()) {
            return Result.healthy("GitLab server has been contacted successfully");
        } else {
            return Result.unhealthy("Unable to contact GitLab server, HTTP code received <"+code+">");
        }
    }
}
