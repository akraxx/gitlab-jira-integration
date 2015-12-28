package fr.mmarie.api.gitlab;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(of = "name")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository {
    @JsonProperty("name")
    private String name;

    @JsonProperty("url")
    private String url;

    @JsonProperty("description")
    private String description;

    @JsonProperty("homepage")
    private String homepage;

    @JsonProperty("git_http_url")
    private String gitHttpUrl;

    @JsonProperty("git_ssh_url")
    private String gitSshUrl;

    @JsonProperty("visibility_level")
    private Long visibilityLevel;
}
