package fr.mmarie.core.jira;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
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

    @NotNull
    @JsonProperty
    private List<TransitionConfiguration> transitions = Lists.newArrayList();

    @VisibleForTesting
    public JiraConfiguration(String username,
                             String password,
                             String url,
                             List<TransitionConfiguration> transitions) {
        this.username = username;
        this.password = password;
        this.url = url;
        this.transitions = transitions;
    }
}
