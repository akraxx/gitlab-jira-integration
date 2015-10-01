package fr.mmarie.core.auth;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.logging.BootstrapLogging;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.servlet.ServletProperties;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.TestProperties;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.security.Principal;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.failBecauseExceptionWasNotThrown;

public class GitLabAuthFilterTestIT extends JerseyTest {

    private static final String PASSWORD = "test-password";

    public static class AuthTestResourceConfig extends DropwizardResourceConfig {
        public AuthTestResourceConfig() {
            super(true, new MetricRegistry());

            register(new AuthDynamicFeature(new GitLabAuthFilter.Builder()
                    .setAuthenticator(new GitLabAuthenticator(PASSWORD))
                    .setUnauthorizedHandler((s, s1) -> Response.status(Response.Status.UNAUTHORIZED).build())
                    .setRealm("GitLab HOOK")
                    .buildAuthFilter()));

            register(new AuthValueFactoryProvider.Binder<>(Principal.class));
            register(RolesAllowedDynamicFeature.class);
            register(AuthResource.class);
        }
    }

    static {
        BootstrapLogging.bootstrap();
    }

    @Override
    protected TestContainerFactory getTestContainerFactory()
            throws TestContainerException {
        return new GrizzlyWebTestContainerFactory();
    }

    @Override
    protected DeploymentContext configureDeployment() {
        forceSet(TestProperties.CONTAINER_PORT, "0");
        return ServletDeploymentContext.builder(new AuthTestResourceConfig())
                .initParam(ServletProperties.JAXRS_APPLICATION_CLASS, AuthTestResourceConfig.class.getName())
                .build();
    }

    @Test
    public void resourceWithoutAuthShouldReturn200() {
        assertThat(target("/test/noauth").request()
                .get(String.class))
                .isEqualTo("hello");
    }

    @Test
    public void resourceWithAuthenticationWithCorrectCredentialsReturn200() {
        String service = "good-svc";
        assertThat(target("/test/protected")
                .queryParam("token", Base64.getEncoder().encodeToString(String.format("%s:%s", service, PASSWORD).getBytes()))
                .request()
                .get(String.class))
                .isEqualTo("'" + service +"' has user privileges");
    }

    @Test
    public void resourceWithAuthenticationWithBadTokenParamReturn401() {
        String service = "bad-svc";
        try {
            target("/test/protected")
                    .queryParam("token", Base64.getEncoder().encodeToString(String.format("%s:%s", service, "bad-pwd").getBytes()))
                    .request()
                    .get(String.class);
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(401);
        }
    }

    @Test
    public void resourceWithAuthenticationWithTokenPatternParamReturn401() {
        String service = "bad-svc";
        try {
            target("/test/protected")
                    .queryParam("token", Base64.getEncoder().encodeToString(String.format("%s %s", service, PASSWORD).getBytes()))
                    .request()
                    .get(String.class);
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(401);
        }
    }

    @Test
    public void resourceWithAuthenticationWithoutTokenParamReturn401() {
        try {
            target("/test/protected")
                    .request()
                    .get(String.class);
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(401);
        }
    }
}