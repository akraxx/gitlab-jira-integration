package fr.mmarie.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fr.mmarie.api.gitlab.Commit;
import fr.mmarie.api.gitlab.Event;
import fr.mmarie.api.gitlab.Repository;
import fr.mmarie.api.gitlab.User;
import fr.mmarie.api.jira.Comment;
import fr.mmarie.core.gitlab.GitLabService;
import fr.mmarie.core.jira.JiraService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import retrofit.Response;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IntegrationServiceTest {

    @Mock
    private GitLabService gitLabService;
    @Mock
    private JiraService jiraService;

    private User user = new User(1L, "John Smith", "john.smith@mocked.com");

    private Response<User> mockedUserResponse = Response.success(user);

    private IntegrationService service;

    @Before
    public void setUp() throws Exception {
        service = new IntegrationService(gitLabService, jiraService);
    }

    @After
    public void tearDown() throws Exception {
        reset(gitLabService, jiraService);
    }

    @Test
    public void commentExistingIssue() throws Exception {
        String issue = "TESTGITLAB-1";
        String repository = "test-repo";
        String commitId = "b6568db1bc1dcd7f8b4d5a946b0b91f9dacd7327";

        when(jiraService.isExistingIssue(issue)).thenReturn(true);

        service.commentIssue(repository,
                Commit.builder().id(commitId).build(),
                new User(1L, "John Smith", "john.smith@mocked.com"),
                issue);

        verify(jiraService, times(1)).commentIssue(eq(issue), any(Comment.class));
    }

    @Test
    public void commentNonExistingIssue() throws Exception {
        String issue = "TESTGITLAB-1";
        String repository = "test-repo";
        String commitId = "b6568db1bc1dcd7f8b4d5a946b0b91f9dacd7327";

        when(jiraService.isExistingIssue(issue)).thenReturn(false);

        service.commentIssue(repository,
                Commit.builder().id(commitId).build(),
                new User(1L, "John Smith", "john.smith@mocked.com"),
                issue);

        verify(jiraService, times(0)).commentIssue(eq(issue), any(Comment.class));
    }

    @Test
    public void commentExistingIssueWithUnavailableJira() throws Exception {
        String issue = "TESTGITLAB-1";
        String repository = "test-repo";
        String commitId = "b6568db1bc1dcd7f8b4d5a946b0b91f9dacd7327";

        when(jiraService.isExistingIssue(issue)).thenReturn(true);
        when(jiraService.commentIssue(eq(issue), any(Comment.class)))
                .thenThrow(new IOException());

        service.commentIssue(repository,
                Commit.builder().id(commitId).build(),
                new User(1L, "John Smith", "john.smith@mocked.com"),
                issue);

        verify(jiraService, times(1)).commentIssue(eq(issue), any(Comment.class));
    }

    @Test
    public void commentIssuesWithEmptyListShouldNotCallGitLabUserService() throws Exception {
        service.commentIssues(new Event(Event.Type.PUSH), new Commit(), Lists.newArrayList());

        verify(gitLabService, times(0)).getUser(anyLong());
    }

    @Test
    public void commentIssuesWithUnavailableGitLabServerShouldThrowAnIOException() throws Exception {
        List<String> issues = ImmutableList.of("TESTGITLAB-1", "TESTGITLAB-2");
        long userId = 1L;
        Event event = Event.builder().type(Event.Type.PUSH).userId(userId).build();

        when(gitLabService.getUser(userId)).thenThrow(new IOException());

        try {
            service.commentIssues(event, new Commit(), issues);
            failBecauseExceptionWasNotThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isNotNull();
        }

        verify(gitLabService, times(1)).getUser(userId);
    }

    @Test
    public void commentIssuesShouldCallChildMethod() throws Exception {
        service = spy(service);

        List<String> issues = ImmutableList.of("TESTGITLAB-1", "TESTGITLAB-2");
        long userId = 1L;
        String repositoryName = "test-repo";
        Event event = Event.builder()
                .type(Event.Type.PUSH)
                .userId(userId)
                .repository(Repository.builder().name(repositoryName).build())
                .build();

        when(gitLabService.getUser(userId)).thenReturn(mockedUserResponse);

        Commit commit = new Commit();
        service.commentIssues(event, commit, issues);

        verify(service, times(1)).commentIssue(repositoryName, commit, user, "TESTGITLAB-1");
        verify(service, times(1)).commentIssue(repositoryName, commit, user, "TESTGITLAB-2");

        verify(gitLabService, times(1)).getUser(userId);
    }
}