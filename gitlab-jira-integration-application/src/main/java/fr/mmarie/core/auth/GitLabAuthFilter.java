package fr.mmarie.core.auth;

import com.google.common.base.Optional;
import com.google.common.io.BaseEncoding;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

@Slf4j
public class GitLabAuthFilter extends AuthFilter<GitLabCredentials, Principal> {

    private GitLabAuthFilter() {
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final String token = requestContext.getUriInfo().getQueryParameters().getFirst("token");
        try {
            if (token != null) {
                final String decoded = new String(
                        BaseEncoding.base64().decode(token),
                        StandardCharsets.UTF_8);
                final int i = decoded.indexOf(':');
                if (i > 0) {
                    final String username = decoded.substring(0, i);
                    final String password = decoded.substring(i + 1);
                    try {
                        GitLabCredentials gitLabCredentials = new GitLabCredentials(username, password);
                        final Optional<Principal> principal = authenticator.authenticate(gitLabCredentials);
                        if (principal.isPresent()) {
                            requestContext.setSecurityContext(new SecurityContext() {
                                @Override
                                public Principal getUserPrincipal() {
                                    return principal.get();
                                }

                                @Override
                                public boolean isUserInRole(String role) {
                                    return authorizer.authorize(principal.get(), role);
                                }

                                @Override
                                public boolean isSecure() {
                                    return requestContext.getSecurityContext().isSecure();
                                }

                                @Override
                                public String getAuthenticationScheme() {
                                    return "GitLab Auth";
                                }
                            });
                            return;
                        }
                    } catch (AuthenticationException e) {
                        log.warn("Error authenticating credentials", e);
                        throw new InternalServerErrorException();
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            log.warn("Error decoding credentials", e);
        }

        throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
    }

    /**
     * Builder for {@link GitLabAuthFilter}.
     * <p>An {@link Authenticator} must be provided during the building process.</p>
     */
    public static class Builder extends
            AuthFilterBuilder<GitLabCredentials, Principal, GitLabAuthFilter> {

        @Override
        protected GitLabAuthFilter newInstance() {
            return new GitLabAuthFilter();
        }
    }
}
