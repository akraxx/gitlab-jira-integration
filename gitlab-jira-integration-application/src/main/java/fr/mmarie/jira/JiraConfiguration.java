package fr.mmarie.jira;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class JiraConfiguration {
    @NotEmpty
    @NotNull
    @JsonProperty
    private String username;

    @NotEmpty
    @NotNull
    @JsonProperty
    private String password;

    @NotEmpty
    @NotNull
    @JsonProperty
    private String url;
}
