package fr.mmarie.api.jira.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import fr.mmarie.api.jira.Comment;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import java.io.IOException;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class CommentResponseTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializesToJSON() throws IOException {
        final CommentResponse commentResponse = new CommentResponse(
                ImmutableList.of(new Comment("This is a comment"), new Comment("This is an other comment"))
        );

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/jira/response/comment.json"), CommentResponse.class));

        assertThat(MAPPER.writeValueAsString(commentResponse)).isEqualTo(expected);
    }

    @Test
    public void deserializesFromJSON() throws IOException {
        final CommentResponse commentResponse = MAPPER.readValue(fixture("fixtures/jira/response/comment.json"), CommentResponse.class);

        assertThat(commentResponse.getComments())
                .hasSameElementsAs(ImmutableList.of(new Comment("This is a comment"), new Comment("This is an other comment")));
    }

}