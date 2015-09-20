package fr.mmarie.core.jira;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Getter
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

    @VisibleForTesting
    public JiraConfiguration(String username, String password, String url) {
        this.username = username;
        this.password = password;
        this.url = url;
    }
}
