package fr.mmarie.resources;

import fr.mmarie.api.gitlab.Event;
import fr.mmarie.core.IntegrationService;
import fr.mmarie.core.auth.GitLabAuthFilter;
import fr.mmarie.core.auth.GitLabAuthenticator;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.security.Principal;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class HookResourceTestIT {

    private static final IntegrationService INTEGRATION_SERVICE = mock(IntegrationService.class);

    private static final String PASSWORD = "test-password";

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new HookResource(INTEGRATION_SERVICE))
            .addProvider(new AuthDynamicFeature(new GitLabAuthFilter.Builder()
                    .setAuthenticator(new GitLabAuthenticator(PASSWORD))
                    .setUnauthorizedHandler((s, s1) -> Response.status(Response.Status.UNAUTHORIZED).build())
                    .setRealm("GitLab HOOK")
                    .buildAuthFilter()))
            .addProvider(new AuthValueFactoryProvider.Binder<>(Principal.class))
            .build();

    @Test
    public void callHookShouldReturnNothing() throws Exception {
        Response response = resources.client().target("/hook")
                .queryParam("token", Base64.getEncoder().encodeToString(String.format("%s:%s", "test-svc", PASSWORD).getBytes()))
                .request()
                .post(Entity.json(new Event(Event.Type.PUSH)));

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(204);
    }

}