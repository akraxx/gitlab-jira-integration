package fr.mmarie.api.jira.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import fr.mmarie.api.jira.Comment;
import fr.mmarie.api.jira.Transition;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode(of = "update")
public class TransitionInput {

    @EqualsAndHashCode(of = "comments")
    @NoArgsConstructor
    public static class Update {
        @JsonProperty("comment")
        private List<CommentWrapper> comments;

        @VisibleForTesting
        public Update(List<CommentWrapper> comments) {
            this.comments = comments;
        }
    }

    @EqualsAndHashCode(of = "comment")
    @NoArgsConstructor
    public static class CommentWrapper {
        @JsonProperty("add")
        private Comment comment;

        @VisibleForTesting
        public CommentWrapper(Comment comment) {
            this.comment = comment;
        }
    }

    @JsonProperty("update")
    private Update update;

    @JsonProperty("transition")
    private Transition transition;

    @VisibleForTesting
    public TransitionInput(Update update, Transition transition) {
        this.update = update;
        this.transition = transition;
    }
}
