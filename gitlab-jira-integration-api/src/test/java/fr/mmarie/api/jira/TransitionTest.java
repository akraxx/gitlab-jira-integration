package fr.mmarie.api.jira;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import java.io.IOException;

import static fr.mmarie.api.jira.TransitionAssert.assertThat;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class TransitionTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializesToJSON() throws IOException {
        final Transition transition = new Transition(15L, "Close");

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/jira/transition.json"), Transition.class));

        assertThat(MAPPER.writeValueAsString(transition)).isEqualTo(expected);
    }

    @Test
    public void deserializesFromJSON() throws IOException {
        final Transition transition = MAPPER.readValue(fixture("fixtures/jira/transition.json"), Transition.class);

        assertThat(transition)
                .hasId(15L)
                .hasName("Close");
    }

    @Test
    public void testToString() throws Exception {
        final Transition transition = new Transition(15L, "Close");

        assertThat(transition.toString()).isEqualTo("Transition(id=15, name=Close)");
    }

}