package fr.mmarie.api.gitlab;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@ToString(of = "id")
@NoArgsConstructor
public class Commit {
    @JsonProperty("id")
    private String id;

    @JsonProperty("message")
    private String message;

    @JsonProperty("timestamp")
    private Date timestamp;

    @JsonProperty("url")
    private String url;

    @JsonProperty("author")
    private Author author;
}
