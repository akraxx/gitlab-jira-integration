package fr.mmarie.api.jira.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.annotations.VisibleForTesting;
import fr.mmarie.api.jira.Comment;
import fr.mmarie.api.jira.Transition;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransitionResponse {

    private List<Transition> transitions;

    @VisibleForTesting
    public TransitionResponse(List<Transition> transitions) {
        this.transitions = transitions;
    }
}
