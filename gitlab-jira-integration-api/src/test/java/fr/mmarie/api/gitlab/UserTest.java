package fr.mmarie.api.gitlab;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.assertj.core.api.StrictAssertions;
import org.junit.Test;

import static fr.mmarie.api.gitlab.UserAssert.assertThat;
import static io.dropwizard.testing.FixtureHelpers.fixture;

public class UserTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializesToJSON() throws Exception {
        final User user = new User(1L, "john_smith", "John Smith");

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/gitlab/user.json"), User.class));

        StrictAssertions.assertThat(MAPPER.writeValueAsString(user)).isEqualTo(expected);
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        final User user = MAPPER.readValue(fixture("fixtures/gitlab/user.json"), User.class);

        assertThat(user)
                .hasId(1L)
                .hasUsername("john_smith")
                .hasName("John Smith");
    }

}