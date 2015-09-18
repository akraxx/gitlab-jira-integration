package fr.mmarie.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
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
    private Type type;

}
