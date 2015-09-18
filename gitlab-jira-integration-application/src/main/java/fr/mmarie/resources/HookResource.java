package fr.mmarie.resources;

import fr.mmarie.api.Event;
import lombok.extern.slf4j.Slf4j;

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

    @POST
    public void hookPush(@HeaderParam(GITLAB_HEADER) String gitLabHeader,
                         Event event) {
        log.info("<{}> Push hook received > {}", gitLabHeader, event);
    }

}
