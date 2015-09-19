package fr.mmarie.resources;

import fr.mmarie.api.gitlab.Event;
import fr.mmarie.jira.JiraService;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class HookResourceTestIT {

    private static final JiraService JIRA_SERVICE = mock(JiraService.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new HookResource(JIRA_SERVICE))
            .build();

    @Test
    public void callHookShouldReturnNothing() throws Exception {
        Response response = resources.client().target("/hook")
                .request()
                .post(Entity.json(new Event(Event.Type.PUSH)));

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(204);
    }

}