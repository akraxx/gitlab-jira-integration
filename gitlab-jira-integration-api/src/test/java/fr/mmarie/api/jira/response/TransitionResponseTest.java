package fr.mmarie.api.jira.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import fr.mmarie.api.jira.Transition;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import java.io.IOException;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class TransitionResponseTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializesToJSON() throws IOException {
        final TransitionResponse transitionResponse = new TransitionResponse(
                ImmutableList.of(new Transition(15L, "Close"), new Transition(18L, "Open"))
        );

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/jira/response/transition.json"), TransitionResponse.class));

        assertThat(MAPPER.writeValueAsString(transitionResponse)).isEqualTo(expected);
    }

    @Test
    public void deserializesFromJSON() throws IOException {
        final TransitionResponse transition = MAPPER.readValue(fixture("fixtures/jira/response/transition.json"), TransitionResponse.class);

        assertThat(transition.getTransitions())
                .hasSameElementsAs(ImmutableList.of(new Transition(15L, "Close"), new Transition(18L, "Open")));

    }

}