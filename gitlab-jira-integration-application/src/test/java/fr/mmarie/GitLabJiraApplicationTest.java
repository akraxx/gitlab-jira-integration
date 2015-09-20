package fr.mmarie;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class GitLabJiraApplicationTest {
    public static final int PORT = 1338;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(PORT);

    @ClassRule
    public static final DropwizardAppRule<GitLabJiraConfiguration> RULE =
            new DropwizardAppRule<>(GitLabJiraApplication.class, ResourceHelpers.resourceFilePath("properties-test.yml"));

    @Test
    public void testHealthyJiraHealthChecks() {
        wireMockRule.stubFor(get(urlEqualTo("/rest/api/2/serverInfo"))
                .withHeader("Authorization", matching("Basic .*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{}")));

        Client client = ClientBuilder.newClient();

        Response healthcheck = client.target(String.format("http://localhost:%d/", RULE.getAdminPort()))
                .path("healthcheck")
                .request()
                .get();

        assertThat(healthcheck.getStatus())
                .isEqualTo(Response.Status.OK.getStatusCode());

        assertThat(healthcheck.readEntity(String.class))
                .contains("\"jira\":{\"healthy\":true,\"message\":\"Jira server has been contacted successfully\"");

        wireMockRule.verify(getRequestedFor(urlEqualTo("/rest/api/2/serverInfo"))
                .withHeader("Authorization", matching("Basic .*")));
    }

    @Test
    public void testUnhealthyJiraHealthChecks() {
        wireMockRule.stubFor(get(urlEqualTo("/rest/api/2/serverInfo"))
                .withHeader("Authorization", matching("Basic .*"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("{}")));

        Client client = ClientBuilder.newClient();

        Response healthcheck = client.target(String.format("http://localhost:%d/", RULE.getAdminPort()))
                .path("healthcheck")
                .request()
                .get();

        assertThat(healthcheck.getStatus())
                .isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

        assertThat(healthcheck.readEntity(String.class))
                .contains("\"jira\":{\"healthy\":false,\"message\":\"Unable to contact JIRA server, HTTP code received <500>\"");

        wireMockRule.verify(getRequestedFor(urlEqualTo("/rest/api/2/serverInfo"))
                .withHeader("Authorization", matching("Basic .*")));
    }
}