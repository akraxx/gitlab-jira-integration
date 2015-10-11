package fr.mmarie.api.jira;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(of = {"id", "name"})
@NoArgsConstructor
@AllArgsConstructor
public class Transition {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

}
