package fr.mmarie;

import fr.mmarie.core.jira.JiraService;
import fr.mmarie.resources.HookResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

/**
 * Created by Maximilien on 17/09/2015.
 */
public class GitLabJiraApplication extends Application<GitLabJiraConfiguration> {

    @Override
    public void run(GitLabJiraConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().register(new HookResource(new JiraService(configuration.getJiraConfiguration())));
    }

    public static void main(String[] args) throws Exception {
        new GitLabJiraApplication().run(args);
    }

}
