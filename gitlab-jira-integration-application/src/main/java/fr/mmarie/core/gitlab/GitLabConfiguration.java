package fr.mmarie.core.gitlab;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class GitLabConfiguration {
    @NotEmpty
    @NotNull
    @JsonProperty("private_token")
    private String privateToken;

    @NotEmpty
    @NotNull
    @JsonProperty
    private String url;

    @VisibleForTesting
    public GitLabConfiguration(String privateToken, String url) {
        this.privateToken = privateToken;
        this.url = url;
    }
}
