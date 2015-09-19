package fr.mmarie.api.gitlab;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.assertj.core.api.StrictAssertions;
import org.junit.Test;

import java.io.IOException;

import static fr.mmarie.api.jira.Assertions.assertThat;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class RepositoryTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializesToJSON() throws IOException {
        final Repository repository = Repository.builder()
                .name("Diaspora")
                .url("git@example.com:mike/diasporadiaspora.git")
                .description("")
                .homepage("http://example.com/mike/diaspora")
                .gitHttpUrl("http://example.com/mike/diaspora.git")
                .gitSshUrl("git@example.com:mike/diaspora.git")
                .visibilityLevel(0L)
                .build();

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/gitlab/repository.json"), Repository.class));

        StrictAssertions.assertThat(MAPPER.writeValueAsString(repository)).isEqualTo(expected);
    }

    @Test
    public void deserializesFromJSON() throws IOException {
        final Repository repository = MAPPER.readValue(fixture("fixtures/gitlab/repository.json"), Repository.class);

        assertThat(repository)
                .hasName("Diaspora")
                .hasUrl("git@example.com:mike/diasporadiaspora.git")
                .hasDescription("")
                .hasHomepage("http://example.com/mike/diaspora")
                .hasGitHttpUrl("http://example.com/mike/diaspora.git")
                .hasGitSshUrl("git@example.com:mike/diaspora.git")
                .hasVisibilityLevel(0L);
    }

    @Test
    public void testToString() throws Exception {
        final Repository repository = Repository.builder()
                .name("Diaspora")
                .url("git@example.com:mike/diasporadiaspora.git")
                .description("")
                .homepage("http://example.com/mike/diaspora")
                .gitHttpUrl("http://example.com/mike/diaspora.git")
                .gitSshUrl("git@example.com:mike/diaspora.git")
                .visibilityLevel(0L)
                .build();

        assertThat(repository.toString()).isEqualTo("Repository(name=Diaspora)");
    }
}