package fr.mmarie.api.jira.response;

import fr.mmarie.api.jira.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class CommentResponse {

    private List<Comment> comments;

}
