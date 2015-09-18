package fr.mmarie.api.jira;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(of = "body")
@NoArgsConstructor
public class Comment {
    @JsonProperty("body")
    private String body;

}
