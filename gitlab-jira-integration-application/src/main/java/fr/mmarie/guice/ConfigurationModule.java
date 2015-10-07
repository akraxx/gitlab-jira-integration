package fr.mmarie.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import fr.mmarie.GitLabJiraConfiguration;
import fr.mmarie.core.gitlab.GitLabConfiguration;
import fr.mmarie.core.jira.JiraConfiguration;

public class ConfigurationModule extends AbstractModule {
    @Override
    protected void configure() {

    }

    @Provides
    public GitLabConfiguration providesGitLabJiraApplication(GitLabJiraConfiguration configuration) {
        return configuration.getGitLabConfiguration();
    }

    @Provides
    public JiraConfiguration providesJiraConfiguration(GitLabJiraConfiguration configuration) {
        return configuration.getJiraConfiguration();
    }
}

