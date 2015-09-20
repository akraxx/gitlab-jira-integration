package fr.mmarie.core.jira;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import fr.mmarie.api.jira.Comment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import retrofit.Response;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static fr.mmarie.Assertions.assertThat;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class JiraServiceTestIT {

    public static final int PORT = 1338;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(PORT);

    public JiraConfiguration jiraConfiguration = new JiraConfiguration("username",
            "password",
            String.format("http://localhost:%d", PORT));

    public JiraService jiraService;

    @Before
    public void setUp() throws Exception {
        jiraService = new JiraService(jiraConfiguration);
    }

    @Test
    public void testGetIssue() throws Exception {
        String issue = "TEST-1";
        wireMockRule.stubFor(get(urlEqualTo("/rest/api/2/issue/" + issue))
                .withHeader("Authorization", matching("Basic .*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{}")));

        Response<Object> response = jiraService.getIssue(issue);

        assertThat(response.code())
                .isEqualTo(200);

        wireMockRule.verify(getRequestedFor(urlEqualTo("/rest/api/2/issue/" + issue))
                .withHeader("Authorization", matching("Basic .*")));
    }

    @Test
    public void testCommentIssue() throws Exception {
        String issue = "TEST-1";
        String body = "This is a comment";
        Comment comment = new Comment(body);
        String mockedComment = fixture("fixtures/jira/comment.json");

        wireMockRule.stubFor(post(urlEqualTo("/rest/api/2/issue/" + issue + "/comment"))
                .withHeader("Authorization", matching("Basic .*"))
                .withRequestBody(equalToJson(mockedComment))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(mockedComment)));

        Response<Comment> response = jiraService.commentIssue(issue, comment);

        assertThat(response.code())
                .isEqualTo(200);

        assertThat(response.body())
                .hasBody(body);

        wireMockRule.verify(postRequestedFor(urlEqualTo("/rest/api/2/issue/" + issue + "/comment"))
                .withHeader("Authorization", matching("Basic .*"))
                .withRequestBody(equalToJson(mockedComment)));
    }

    @Test
    public void testServerInfo() throws Exception {
        wireMockRule.stubFor(get(urlEqualTo("/rest/api/2/serverInfo"))
                .withHeader("Authorization", matching("Basic .*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{}")));

        Response<Map<String, Object>> response = jiraService.serverInfo();

        assertThat(response.code())
                .isEqualTo(200);

        wireMockRule.verify(getRequestedFor(urlEqualTo("/rest/api/2/serverInfo"))
                .withHeader("Authorization", matching("Basic .*")));
    }
}