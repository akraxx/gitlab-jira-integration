package fr.mmarie.core.jira;

import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import fr.mmarie.api.jira.Comment;
import fr.mmarie.api.jira.input.TransitionInput;
import org.assertj.core.data.MapEntry;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import retrofit.Response;

import java.util.List;
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

    public static final int PORT = 1520;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(PORT);

    public JiraConfiguration jiraConfiguration = new JiraConfiguration("username",
            "password",
            String.format("http://localhost:%d", PORT),
            ImmutableList.of(new TransitionConfiguration("Close", ImmutableList.of("close", "fix"))));

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

    @Test
    public void isExistingIssueWithAGoodOne() throws Exception {
        String issue = "TEST-1";
        wireMockRule.stubFor(get(urlEqualTo("/rest/api/2/issue/" + issue))
                .withHeader("Authorization", matching("Basic .*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{}")));

        assertThat(jiraService.isExistingIssue(issue)).isTrue();

        wireMockRule.verify(getRequestedFor(urlEqualTo("/rest/api/2/issue/" + issue))
                .withHeader("Authorization", matching("Basic .*")));
    }

    @Test
    public void isExistingIssueWithABadOne() throws Exception {
        String issue = "TEST-1";
        wireMockRule.stubFor(get(urlEqualTo("/rest/api/2/issue/" + issue))
                .withHeader("Authorization", matching("Basic .*"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("{}")));

        assertThat(jiraService.isExistingIssue(issue)).isFalse();

        wireMockRule.verify(getRequestedFor(urlEqualTo("/rest/api/2/issue/" + issue))
                .withHeader("Authorization", matching("Basic .*")));
    }

    @Test
    public void isExistingIssueWithAServerError() throws Exception {
        String issue = "TEST-1";
        wireMockRule.stubFor(get(urlEqualTo("/rest/api/2/issue/" + issue))
                .withHeader("Authorization", matching("Basic .*"))
                .willReturn(aResponse()
                        .withFault(Fault.EMPTY_RESPONSE)));

        assertThat(jiraService.isExistingIssue(issue)).isFalse();

        wireMockRule.verify(getRequestedFor(urlEqualTo("/rest/api/2/issue/" + issue))
                .withHeader("Authorization", matching("Basic .*")));
    }

    @Test
    public void isIssueAlreadyCommentedWithAServerError() throws Exception {
        String issue = "TEST-1";
        wireMockRule.stubFor(get(urlEqualTo("/rest/api/2/issue/" + issue + "/comment"))
                .withHeader("Authorization", matching("Basic .*"))
                .willReturn(aResponse()
                        .withFault(Fault.EMPTY_RESPONSE)));

        assertThat(jiraService.isIssueAlreadyCommented(issue, "commitId")).isTrue();

        wireMockRule.verify(getRequestedFor(urlEqualTo("/rest/api/2/issue/" + issue + "/comment"))
                .withHeader("Authorization", matching("Basic .*")));
    }

    @Test
    public void alreadyCommentedIssueShouldReturnTrue() throws Exception {
        String issue = "TEST-1";
        wireMockRule.stubFor(get(urlEqualTo("/rest/api/2/issue/" + issue + "/comment"))
                .withHeader("Authorization", matching("Basic .*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{ \"comments\" : [{\"body\":\"commit is : commitId\"}] }")));

        assertThat(jiraService.isIssueAlreadyCommented(issue, "commitId")).isTrue();

        wireMockRule.verify(getRequestedFor(urlEqualTo("/rest/api/2/issue/" + issue + "/comment"))
                .withHeader("Authorization", matching("Basic .*")));
    }

    @Test
    public void notCommentedIssueShouldReturnFalse() throws Exception {
        String issue = "TEST-1";
        wireMockRule.stubFor(get(urlEqualTo("/rest/api/2/issue/" + issue + "/comment"))
                .withHeader("Authorization", matching("Basic .*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{ \"comments\" : [{\"body\":\"commitId\"}, {\"body\":\"commitId2\"}] }")));

        assertThat(jiraService.isIssueAlreadyCommented(issue, "newOne")).isFalse();

        wireMockRule.verify(getRequestedFor(urlEqualTo("/rest/api/2/issue/" + issue + "/comment"))
                .withHeader("Authorization", matching("Basic .*")));
    }

    @Test
    public void isExistingTransitionWithNonExistingTransaction() {
        String issue = "TEST-1";
        wireMockRule.stubFor(get(urlEqualTo("/rest/api/2/issue/" + issue + "/transitions"))
                .withHeader("Authorization", matching("Basic .*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{ \"transitions\" : [{\"id\":\"15\", \"name\":\"Close\"}] }")));

        assertThat(jiraService.isExistingTransition(issue, "test")).isFalse();

        wireMockRule.verify(getRequestedFor(urlEqualTo("/rest/api/2/issue/" + issue + "/transitions"))
                .withHeader("Authorization", matching("Basic .*")));
    }

    @Test
    public void isExistingTransitionWithExistingTransaction() {
        String issue = "TEST-1";
        wireMockRule.stubFor(get(urlEqualTo("/rest/api/2/issue/" + issue + "/transitions"))
                .withHeader("Authorization", matching("Basic .*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{ \"transitions\" : [{\"id\":\"15\", \"name\":\"Close\"}, {\"id\":\"16\", \"name\":\"Open\"}] }")));

        assertThat(jiraService.isExistingTransition(issue, "close")).isTrue();

        wireMockRule.verify(getRequestedFor(urlEqualTo("/rest/api/2/issue/" + issue + "/transitions"))
                .withHeader("Authorization", matching("Basic .*")));
    }

    @Test
    public void transitionOnIssueWithBadResponseStatus() throws Exception {
        String issue = "TEST-1";
        wireMockRule.stubFor(post(urlEqualTo("/rest/api/2/issue/" + issue + "/transitions"))
                .withHeader("Authorization", matching("Basic .*"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("Bad request !")));

        jiraService.transitionOnIssue(issue, new TransitionInput());
    }
}
