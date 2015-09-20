package fr.mmarie.api.gitlab;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import java.io.IOException;

import static fr.mmarie.api.gitlab.AuthorAssert.assertThat;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthorTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializesToJSON() throws IOException {
        final Author author = new Author("akraxx", "contact@mmarie.fr");

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/gitlab/author.json"), Author.class));

        assertThat(MAPPER.writeValueAsString(author)).isEqualTo(expected);
    }

    @Test
    public void deserializesFromJSON() throws IOException {
        final Author author = MAPPER.readValue(fixture("fixtures/gitlab/author.json"), Author.class);

        assertThat(author)
                .hasName("akraxx")
                .hasEmail("contact@mmarie.fr");
    }

    @Test
    public void testToString() throws Exception {
        final Author author = new Author("akraxx", "contact@mmarie.fr");

        assertThat(author.toString()).isEqualTo("Author(name=akraxx)");
    }
}