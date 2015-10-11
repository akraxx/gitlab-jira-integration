package fr.mmarie.api.jira.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import fr.mmarie.api.jira.Comment;
import fr.mmarie.api.jira.Transition;
import fr.mmarie.api.jira.response.TransitionResponse;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import java.io.IOException;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class TransitionInputTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializesToJSON() throws IOException {
        TransitionInput.CommentWrapper commentWrapper = new TransitionInput.CommentWrapper(
                new Comment("Bug has been fixed."));

        final TransitionInput transitionResponse = new TransitionInput(
                new TransitionInput.Update(ImmutableList.of(commentWrapper)),
                new Transition(15L, "Close")
        );

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/jira/input/transition.json"), TransitionInput.class));

        assertThat(MAPPER.writeValueAsString(transitionResponse)).isEqualTo(expected);
    }

    @Test
    public void deserializesFromJSON() throws IOException {
        TransitionInput.CommentWrapper commentWrapper = new TransitionInput.CommentWrapper(
                new Comment("Bug has been fixed."));

        final TransitionInput expected = new TransitionInput(
                new TransitionInput.Update(ImmutableList.of(commentWrapper)),
                new Transition(15L, "Close")
        );

        final TransitionInput transition = MAPPER.readValue(fixture("fixtures/jira/input/transition.json"), TransitionInput.class);

        assertThat(transition)
                .isEqualTo(expected);

    }

    @Test
    public void testToString() throws Exception {
        TransitionInput.CommentWrapper commentWrapper = new TransitionInput.CommentWrapper(
                new Comment("Bug has been fixed."));

        final TransitionInput expected = new TransitionInput(
                new TransitionInput.Update(ImmutableList.of(commentWrapper)),
                new Transition(15L, "Close")
        );

        assertThat(expected.toString())
                .isEqualTo("TransitionInput(transition=Transition(id=15, name=Close))");
    }

}