package fr.mmarie.api.gitlab;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.assertj.core.api.StrictAssertions;
import org.junit.Test;

import static fr.mmarie.api.Utils.getDateFromString;
import static fr.mmarie.api.jira.Assertions.assertThat;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class CommitTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializesToJSON() throws Exception {
        final Commit commit = Commit.builder()
                .id("b6568db1bc1dcd7f8b4d5a946b0b91f9dacd7327")
                .message("Update Catalan translation to e38cb41.")
                .timestamp(getDateFromString("2011-12-12T14:27:31+02:00"))
                .url("http://example.com/mike/diaspora/commit/b6568db1bc1dcd7f8b4d5a946b0b91f9dacd7327")
                .build();

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/gitlab/commit.json"), Commit.class));

        StrictAssertions.assertThat(MAPPER.writeValueAsString(commit)).isEqualTo(expected);
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        final Commit commit = MAPPER.readValue(fixture("fixtures/gitlab/commit.json"), Commit.class);

        assertThat(commit)
                .hasId("b6568db1bc1dcd7f8b4d5a946b0b91f9dacd7327")
                .hasMessage("Update Catalan translation to e38cb41.")
                .hasUrl("http://example.com/mike/diaspora/commit/b6568db1bc1dcd7f8b4d5a946b0b91f9dacd7327");
    }

    @Test
    public void testToString() throws Exception {
        final Commit commit = Commit.builder()
                .id("b6568db1bc1dcd7f8b4d5a946b0b91f9dacd7327")
                .message("Update Catalan translation to e38cb41.")
                .timestamp(getDateFromString("2011-12-12T14:27:31+02:00"))
                .url("http://example.com/mike/diaspora/commit/b6568db1bc1dcd7f8b4d5a946b0b91f9dacd7327")
                .build();

        assertThat(commit.toString()).isEqualTo("Commit(id=b6568db1bc1dcd7f8b4d5a946b0b91f9dacd7327)");
    }
}