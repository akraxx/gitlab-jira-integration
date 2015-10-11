package fr.mmarie.api.jira.response;

import com.google.common.annotations.VisibleForTesting;
import fr.mmarie.api.jira.Comment;
import fr.mmarie.api.jira.Transition;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class TransitionResponse {

    private List<Transition> transitions;

    @VisibleForTesting
    public TransitionResponse(List<Transition> transitions) {
        this.transitions = transitions;
    }
}
