package fr.mmarie.api.jira.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.annotations.VisibleForTesting;
import fr.mmarie.api.jira.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentResponse {

    private List<Comment> comments;

    @VisibleForTesting
    public CommentResponse(List<Comment> comments) {
        this.comments = comments;
    }
}
