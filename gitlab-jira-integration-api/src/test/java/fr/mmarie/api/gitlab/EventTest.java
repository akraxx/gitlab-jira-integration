package fr.mmarie.api.gitlab;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static fr.mmarie.api.jira.Assertions.assertThat;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializesToJSON() throws Exception {
        final Event event = Event.builder()
                .type(Event.Type.PUSH)
                .before("95790bf891e76fee5e1747ab589903a6a1f80f22")
                .after("da1560886d4f094c3e6c9ef40349f7d38b5d27d7")
                .ref("refs/heads/master")
                .userId(4L)
                .userName("John Smith")
                .userEmail("john@example.com")
                .projectId(15L)
                .totalCommitsCount(4L)
                .build();

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/gitlab/event.json"), Event.class));

        assertThat(MAPPER.writeValueAsString(event)).isEqualTo(expected);
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        final Event event = MAPPER.readValue(fixture("fixtures/gitlab/event.json"), Event.class);

        assertThat(event)
                .hasType(Event.Type.PUSH)
                .hasBefore("95790bf891e76fee5e1747ab589903a6a1f80f22")
                .hasAfter("da1560886d4f094c3e6c9ef40349f7d38b5d27d7")
                .hasRef("refs/heads/master")
                .hasUserId(4L)
                .hasUserName("John Smith")
                .hasUserEmail("john@example.com")
                .hasProjectId(15L)
                .hasTotalCommitsCount(4L);
    }

    @Test
    public void testToString() throws Exception {
        final Event event = new Event(Event.Type.PUSH);

        assertThat(event.toString()).isEqualTo("Event(type=PUSH)");
    }

}