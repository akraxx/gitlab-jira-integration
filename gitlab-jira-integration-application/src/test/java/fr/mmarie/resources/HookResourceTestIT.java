package fr.mmarie.resources;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.mmarie.api.gitlab.Event;
import fr.mmarie.core.IntegrationService;
import fr.mmarie.core.auth.GitLabAuthFilter;
import fr.mmarie.core.auth.GitLabAuthenticator;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;

import javax.validation.Validation;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.security.Principal;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class HookResourceTestIT {

    private static final IntegrationService INTEGRATION_SERVICE = mock(IntegrationService.class);

    private static final String PASSWORD = "test-password";

    private static final Environment ENVIRONMENT = new Environment("mocked-env",
            new ObjectMapper(),
            Validation.buildDefaultValidatorFactory().getValidator(),
            new MetricRegistry(),
            ClassLoader.getSystemClassLoader());

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new HookResource(INTEGRATION_SERVICE, ENVIRONMENT))
            .addProvider(new AuthDynamicFeature(new GitLabAuthFilter.Builder()
                    .setAuthenticator(new GitLabAuthenticator(PASSWORD))
                    .setUnauthorizedHandler((s, s1) -> Response.status(Response.Status.UNAUTHORIZED).build())
                    .setRealm("GitLab HOOK")
                    .buildAuthFilter()))
            .addProvider(new AuthValueFactoryProvider.Binder<>(Principal.class))
            .build();

    @After
    public void tearDown() throws Exception {
        reset(INTEGRATION_SERVICE);
    }

    @Test
    public void hook_WithPushEvent_ShouldPerformIt() throws Exception {
        Event event = new Event(Event.Type.PUSH);
        Response response = resources.client().target("/hook")
                .queryParam("token", Base64.getEncoder().encodeToString(String.format("%s:%s", "test-svc", PASSWORD).getBytes()))
                .request()
                .post(Entity.json(event));

        verify(INTEGRATION_SERVICE, timeout(100)).performPushEvent(event);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(204);
    }

    @Test
    public void hook_WithTagPushEvent_ShouldPerformIt() throws Exception {
        Event event = new Event(Event.Type.TAG_PUSH);
        Response response = resources.client().target("/hook")
                .queryParam("token", Base64.getEncoder().encodeToString(String.format("%s:%s", "test-svc", PASSWORD).getBytes()))
                .request()
                .post(Entity.json(event));

        verify(INTEGRATION_SERVICE, timeout(100)).performPushEvent(event);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(204);
    }

    @Test
    public void hook_WithMergeEvent_ShouldDoNothing() throws Exception {
        Event event = new Event(Event.Type.MERGE_REQUEST);
        Response response = resources.client().target("/hook")
                .queryParam("token", Base64.getEncoder().encodeToString(String.format("%s:%s", "test-svc", PASSWORD).getBytes()))
                .request()
                .post(Entity.json(event));

        verify(INTEGRATION_SERVICE, times(0)).performPushEvent(event);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(204);
    }
}