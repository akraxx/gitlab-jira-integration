package fr.mmarie.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@ToString(of = "name")
@NoArgsConstructor
public class Author {
    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;
}
