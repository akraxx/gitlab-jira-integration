package fr.mmarie;

import fr.mmarie.core.jira.JiraService;
import fr.mmarie.health.JiraHealthCheck;
import fr.mmarie.resources.HookResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class GitLabJiraApplication extends Application<GitLabJiraConfiguration> {

    @Override
    public void run(GitLabJiraConfiguration configuration, Environment environment) throws Exception {
        JiraService jiraService = new JiraService(configuration.getJiraConfiguration());
        environment.jersey().register(new HookResource(jiraService));
        environment.healthChecks().register("jira", new JiraHealthCheck(jiraService));
    }

    public static void main(String[] args) throws Exception {
        new GitLabJiraApplication().run(args);
    }

}
