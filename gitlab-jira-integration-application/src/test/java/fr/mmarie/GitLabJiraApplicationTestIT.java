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

public class GitLabJiraApplicationTestIT {
    public static final int PORT_JIRA = 1338;
    public static final int PORT_GITLAB = 1339;

    @Rule
    public WireMockRule wireMockRuleJira = new WireMockRule(PORT_JIRA);

    @Rule
    public WireMockRule wireMockRuleGitLab = new WireMockRule(PORT_GITLAB);

    @ClassRule
    public static final DropwizardAppRule<GitLabJiraConfiguration> RULE =
            new DropwizardAppRule<>(GitLabJiraApplication.class, ResourceHelpers.resourceFilePath("properties-test.yml"));

    @Test
    public void testHealthyHealthChecks() {
        wireMockRuleJira.stubFor(get(urlEqualTo("/rest/api/2/serverInfo"))
                .withHeader("Authorization", matching("Basic .*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{}")));

        wireMockRuleGitLab.stubFor(get(urlEqualTo("/api/v3/user?private_token="
                + RULE.getConfiguration().getGitLabConfiguration().getPrivateToken()))
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
                .contains("\"jira\":{\"healthy\":true,\"message\":\"Jira server has been contacted successfully\"",
                        "\"gitlab\":{\"healthy\":true,\"message\":\"GitLab server has been contacted successfully\"}");

        wireMockRuleJira.verify(getRequestedFor(urlEqualTo("/rest/api/2/serverInfo"))
                .withHeader("Authorization", matching("Basic .*")));

        wireMockRuleGitLab.verify(getRequestedFor(urlEqualTo("/api/v3/user?private_token="
                + RULE.getConfiguration().getGitLabConfiguration().getPrivateToken())));
    }

    @Test
    public void testUnhealthyHealthChecks() {
        wireMockRuleJira.stubFor(get(urlEqualTo("/rest/api/2/serverInfo"))
                .withHeader("Authorization", matching("Basic .*"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("{}")));

        wireMockRuleGitLab.stubFor(get(urlEqualTo("/api/v3/user?private_token="
                + RULE.getConfiguration().getGitLabConfiguration().getPrivateToken()))
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
                .contains("\"jira\":{\"healthy\":false,\"message\":\"Unable to contact JIRA server, HTTP code received <500>\"",
                        "\"gitlab\":{\"healthy\":false,\"message\":\"Unable to contact GitLab server, HTTP code received <500>\"");

        wireMockRuleJira.verify(getRequestedFor(urlEqualTo("/rest/api/2/serverInfo"))
                .withHeader("Authorization", matching("Basic .*")));

        wireMockRuleGitLab.verify(getRequestedFor(urlEqualTo("/api/v3/user?private_token="
                + RULE.getConfiguration().getGitLabConfiguration().getPrivateToken())));
    }
}