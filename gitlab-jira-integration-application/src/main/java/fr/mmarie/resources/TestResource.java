package fr.mmarie.resources;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import fr.mmarie.api.jira.Comment;
import fr.mmarie.api.jira.Transition;
import fr.mmarie.api.jira.input.TransitionInput;
import fr.mmarie.core.jira.JiraService;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("/test")
@Slf4j
public class TestResource {

    private final JiraService jiraService;

    @Inject
    public TestResource(JiraService jiraService) {
        this.jiraService = jiraService;
    }

    @GET
    @Path("/get/{issue}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Transition> getTransitions(@PathParam("issue") String issue) throws IOException {
        return jiraService.getTransitionsOfIssue(issue).getTransitions();
    }

    @GET
    @Path("/post/{issue}")
    @Produces(MediaType.APPLICATION_JSON)
    public void postTransitions(@PathParam("issue") String issue) throws IOException {
        TransitionInput.CommentWrapper commentWrapper = new TransitionInput.CommentWrapper(
                new Comment("Bug has been fixed."));

        final TransitionInput transitionInput = new TransitionInput(
                new TransitionInput.Update(ImmutableList.of(commentWrapper)),
                new Transition(52L, "Done")
        );

        jiraService.transitionOnIssue(issue, transitionInput);
    }
}
