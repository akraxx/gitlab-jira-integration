package fr.mmarie.resources;

import fr.mmarie.api.gitlab.Event;
import fr.mmarie.core.IntegrationService;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/hook")
@Slf4j
public class HookResource {

    public static final String GITLAB_HEADER = "X-Gitlab-Event";

    private final IntegrationService service;

    public HookResource(IntegrationService service) {
        this.service = service;
    }

    @POST
    public void hook(@HeaderParam(GITLAB_HEADER) String gitLabHeader,
                         @Valid Event event) {
        ((Runnable) () -> {
            log.info("<{}> Push hook received > {}", gitLabHeader, event);
            switch (event.getType()) {
                case PUSH:
                    service.performPushEvent(event);
                    break;
            }
        }).run();
    }

}
