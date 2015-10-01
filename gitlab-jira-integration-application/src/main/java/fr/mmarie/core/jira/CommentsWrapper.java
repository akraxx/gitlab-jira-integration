package fr.mmarie.core.jira;

import fr.mmarie.api.jira.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class CommentsWrapper {

    private List<Comment> comments;
}
