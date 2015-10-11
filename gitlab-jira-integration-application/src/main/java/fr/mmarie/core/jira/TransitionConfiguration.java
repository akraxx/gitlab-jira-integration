package fr.mmarie.core.jira;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

public class TransitionConfiguration {
    @NotEmpty
    @NotNull
    @JsonProperty
    private String name;

    @NotEmpty
    @NotNull
    @JsonProperty
    private List<String> keywords;
}
