package fr.mmarie;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mmarie.core.gitlab.GitLabConfiguration;
import fr.mmarie.core.jira.JiraConfiguration;
import io.dropwizard.Configuration;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
public class GitLabJiraConfiguration extends Configuration {
    @NotNull
    @JsonProperty("jira")
    private JiraConfiguration jiraConfiguration;

    @NotNull
    @JsonProperty("gitlab")
    private GitLabConfiguration gitLabConfiguration;

    @NotNull
    @JsonProperty("password")
    @Length(min = 5)
    private String password;
}
