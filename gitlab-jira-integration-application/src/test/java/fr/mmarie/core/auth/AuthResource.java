package fr.mmarie.core.auth;


import io.dropwizard.auth.Auth;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.security.Principal;

@Path("/test/")
@Produces(MediaType.TEXT_PLAIN)
public class AuthResource {

    @GET
    @Path("noauth")
    public String hello() {
        return "hello";
    }

    @GET
    @Path("protected")
    public String protectedEndPoint(@Auth Principal principal) {
        return "'" + principal.getName() + "' has user privileges";
    }

}
