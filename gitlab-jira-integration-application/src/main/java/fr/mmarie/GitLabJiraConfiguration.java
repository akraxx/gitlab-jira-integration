package fr.mmarie;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mmarie.core.jira.JiraConfiguration;
import io.dropwizard.Configuration;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class GitLabJiraConfiguration extends Configuration {
    @NotNull
    @JsonProperty("jira")
    private JiraConfiguration jiraConfiguration;
}
