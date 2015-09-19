package fr.mmarie.api.jira;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import java.io.IOException;

import static fr.mmarie.api.jira.Assertions.assertThat;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class CommentTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializesToJSON() throws IOException {
        final Comment comment = new Comment("This is a comment");

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/jira/comment.json"), Comment.class));

        assertThat(MAPPER.writeValueAsString(comment)).isEqualTo(expected);
    }

    @Test
    public void deserializesFromJSON() throws IOException {
        final Comment comment = MAPPER.readValue(fixture("fixtures/jira/comment.json"), Comment.class);

        assertThat(comment).hasBody("This is a comment");
    }

    @Test
    public void testToString() throws Exception {
        final Comment comment = new Comment("This is a comment");

        assertThat(comment.toString()).isEqualTo("Comment(body=This is a comment)");
    }
}