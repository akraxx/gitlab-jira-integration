package fr.mmarie;

import fr.mmarie.core.IntegrationService;
import fr.mmarie.core.gitlab.GitLabService;
import fr.mmarie.core.jira.JiraService;
import fr.mmarie.health.GitLabHealthCheck;
import fr.mmarie.health.JiraHealthCheck;
import fr.mmarie.resources.HookResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class GitLabJiraApplication extends Application<GitLabJiraConfiguration> {

    @Override
    public void run(GitLabJiraConfiguration configuration, Environment environment) throws Exception {
        JiraService jiraService = new JiraService(configuration.getJiraConfiguration());
        GitLabService gitLabService = new GitLabService(configuration.getGitLabConfiguration());

        environment.jersey().register(new HookResource(new IntegrationService(gitLabService, jiraService)));
        environment.healthChecks().register("jira", new JiraHealthCheck(jiraService));
        environment.healthChecks().register("gitlab", new GitLabHealthCheck(gitLabService));
    }

    public static void main(String[] args) throws Exception {
        new GitLabJiraApplication().run(args);
    }

}
