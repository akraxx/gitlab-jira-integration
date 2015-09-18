package fr.mmarie.resources;

import fr.mmarie.api.gitlab.Event;
import fr.mmarie.jira.JiraService;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Created by Maximilien on 17/09/2015.
 */
@Path("/hook")
@Slf4j
public class HookResource {

    public static final String GITLAB_HEADER = "X-Gitlab-Event";

    private final JiraService jiraService;

    public HookResource(JiraService jiraService) {
        this.jiraService = jiraService;
    }

    @POST
    public void hookPush(@HeaderParam(GITLAB_HEADER) String gitLabHeader,
                         @Valid Event event) {
        log.info("<{}> Push hook received > {}", gitLabHeader, event);
    }

}
