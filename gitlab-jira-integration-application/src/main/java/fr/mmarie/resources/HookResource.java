package fr.mmarie.resources;

import com.google.inject.Inject;
import fr.mmarie.api.gitlab.Event;
import fr.mmarie.core.IntegrationService;
import io.dropwizard.auth.Auth;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.validation.Valid;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.security.Principal;
import java.util.Date;

@Path("/hook")
@Slf4j
public class HookResource {

    public static final String MDC_KEY_HOOK_ID = "hookId";

    public static final String GITLAB_HEADER = "X-Gitlab-Event";

    private final IntegrationService service;

    @Inject
    public HookResource(IntegrationService service) {
        this.service = service;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void hook(@Auth Principal principal,
                     @HeaderParam(GITLAB_HEADER) String gitLabHeader,
                     @Valid Event event) {
        new Thread(() -> {
            initMDC(principal);
            log.info("Push hook received > {}", event);
            switch (event.getType()) {
                case PUSH:
                    service.performPushEvent(event);
                    break;
            }
            clearMDC();
        }).start();
    }

    private void initMDC(Principal principal) {
        final String requestId = String.format("%s-%d",
                principal.getName(),
                new Date().getTime());
        MDC.put(MDC_KEY_HOOK_ID, requestId);
        log.info("initMDC(): initialized new requestId <{}>", requestId);
    }

    private void clearMDC() {
        MDC.remove(MDC_KEY_HOOK_ID);
    }

}