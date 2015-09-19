package fr.mmarie.api.gitlab;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@ToString(of = "type")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

    public enum Type {
        PUSH("push"),
        TAG_PUSH("tag_push"),
        ISSUE("issue"),
        NOTE("note"),
        MERGE_REQUEST("merge_request");

        private String label;

        Type(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    @JsonProperty("object_kind")
    @NotNull
    private Type type;

    @JsonProperty("before")
    private String before;

    @JsonProperty("after")
    private String after;

    @JsonProperty("ref")
    private String ref;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_email")
    private String userEmail;

    @JsonProperty("project_id")
    private Long projectId;

    @JsonProperty("repository")
    private Repository repository;

    @JsonProperty("commits")
    private List<Commit> commits;

}
