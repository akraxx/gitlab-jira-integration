package fr.mmarie.health;

import com.google.inject.Inject;
import fr.mmarie.core.gitlab.GitLabService;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;

import javax.ws.rs.core.Response;

public class GitLabHealthCheck extends NamedHealthCheck {

    private final GitLabService gitLabService;

    @Inject
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

    @Override
    public String getName() {
        return "gitlab";
    }
}
