package fr.mmarie;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GitLabJiraConfigurationTest {

    @Test
    public void jiraConfigurationShouldBeNullByDefault() {
        GitLabJiraConfiguration gitLabJiraConfiguration = new GitLabJiraConfiguration();
        assertThat(gitLabJiraConfiguration.getJiraConfiguration()).isNull();
    }
}