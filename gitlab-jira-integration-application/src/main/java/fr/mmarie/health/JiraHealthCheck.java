package fr.mmarie.health;

import com.google.inject.Inject;
import fr.mmarie.core.jira.JiraService;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;

import javax.ws.rs.core.Response;

public class JiraHealthCheck extends NamedHealthCheck {

    private final JiraService jiraService;

    @Inject
    public JiraHealthCheck(JiraService jiraService) {
        this.jiraService = jiraService;
    }

    @Override
    protected Result check() throws Exception {
        int code = jiraService.serverInfo().code();
        if(code == Response.Status.OK.getStatusCode()) {
            return Result.healthy("Jira server has been contacted successfully");
        } else {
            return Result.unhealthy("Unable to contact JIRA server, HTTP code received <"+code+">");
        }
    }

    @Override
    public String getName() {
        return "jira";
    }
}
