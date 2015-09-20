package fr.mmarie.health;

import com.codahale.metrics.health.HealthCheck;
import fr.mmarie.core.jira.JiraService;

import javax.ws.rs.core.Response;

public class JiraHealthCheck extends HealthCheck {

    private final JiraService jiraService;

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
}
