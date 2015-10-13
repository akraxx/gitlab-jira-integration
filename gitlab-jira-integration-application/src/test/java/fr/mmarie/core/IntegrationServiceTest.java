package fr.mmarie.core;

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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import retrofit.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
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

        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);
        when(jiraService.isExistingIssue(issue)).thenReturn(true);
        when(jiraService.isIssueAlreadyCommented(issue, commitId)).thenReturn(false);

        service.commentIssue(repository,
                new User(1L, "John Smith", "john.smith@mocked.com"),
                Collections.singletonList(Commit.builder().id(commitId).build()),
                issue);

        verify(jiraService, times(1)).commentIssue(eq(issue), commentArgumentCaptor.capture());
        Comment comment = commentArgumentCaptor.getValue();
        assertThat(comment.getBody())
                .contains("unknown date of commit");
    }

    @Test
    public void commentExistingIssueWithoutUsername() throws Exception {
        String issue = "TESTGITLAB-1";
        String repository = "test-repo";
        String commitId = "b6568db1bc1dcd7f8b4d5a946b0b91f9dacd7327";

        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);
        when(jiraService.isExistingIssue(issue)).thenReturn(true);
        when(jiraService.isIssueAlreadyCommented(issue, commitId)).thenReturn(false);

        String message = "Very nice commit !";
        String url = "http://test.gitlab.fr/";
        Date date = new Date();

        service.commentIssue(repository,
                new User(1L, null, "john.smith@mocked.com"),
                Collections.singletonList(Commit.builder()
                        .id(commitId)
                        .message(message)
                        .url(url)
                        .timestamp(date)
                        .build()),
                issue);

        verify(jiraService, times(1)).commentIssue(eq(issue), commentArgumentCaptor.capture());
        Comment comment = commentArgumentCaptor.getValue();
        assertThat(comment.getBody())
                .contains("john.smith@mocked.com", repository, message, url, IntegrationService.DATE_FORMAT.format(date), message);
    }

    @Test
    public void commentNonExistingIssue() throws Exception {
        String issue = "TESTGITLAB-1";
        String repository = "test-repo";
        String commitId = "b6568db1bc1dcd7f8b4d5a946b0b91f9dacd7327";

        when(jiraService.isExistingIssue(issue)).thenReturn(false);
        when(jiraService.isIssueAlreadyCommented(issue, commitId)).thenReturn(false);

        service.commentIssue(repository,
                new User(1L, "John Smith", "john.smith@mocked.com"),
                Collections.singletonList(Commit.builder().id(commitId).build()),
                issue);

        verify(jiraService, times(0)).commentIssue(eq(issue), any(Comment.class));
    }

    @Test
    public void commentExistingIssueWithUnavailableJira() throws Exception {
        String issue = "TESTGITLAB-1";
        String repository = "test-repo";
        String commitId = "b6568db1bc1dcd7f8b4d5a946b0b91f9dacd7327";

        when(jiraService.isExistingIssue(issue)).thenReturn(true);
        when(jiraService.isIssueAlreadyCommented(issue, commitId)).thenReturn(false);
        when(jiraService.commentIssue(eq(issue), any(Comment.class)))
                .thenThrow(new IOException());

        service.commentIssue(repository,
                new User(1L, "John Smith", "john.smith@mocked.com"),
                Collections.singletonList(Commit.builder().id(commitId).build()),
                issue);

        verify(jiraService, times(1)).commentIssue(eq(issue), any(Comment.class));
    }

    @Test
    public void issueShouldNotBeCommentedWhenItsAlreadyIs() throws IOException {
        String issue = "TESTGITLAB-1";
        String repository = "test-repo";
        String commitId = "b6568db1bc1dcd7f8b4d5a946b0b91f9dacd7327";

        when(jiraService.isExistingIssue(issue)).thenReturn(true);
        when(jiraService.isIssueAlreadyCommented(issue, commitId)).thenReturn(true);

        service.commentIssue(repository,
                new User(1L, "John Smith", "john.smith@mocked.com"),
                Collections.singletonList(Commit.builder().id(commitId).build()),
                issue);

        verify(jiraService, times(0)).commentIssue(eq(issue), any(Comment.class));
    }

    @Test
    public void performPushEventShouldCheckCommit() throws IOException {
        service = spy(service);

        String commitId1 = "b6568db1bc1dcd7f8b4d5a946b0b91f9dacd7327qgh";
        Commit commit1 = Commit.builder()
                .message("#GITLAB-1")
                .id(commitId1)
                .build();
        String commitId2 = "b6iib8db1bc1doqzjbj4d5a946b0b91f9dacd73qzd5";
        Commit commit2 = Commit.builder()
                .message("#GITLAB-1,#GITLAB-2")
                .id(commitId2)
                .build();

        when(jiraService.extractIssuesFromMessage("#GITLAB-1")).thenReturn(Collections.singletonList("GITLAB-1"));
        when(jiraService.extractIssuesFromMessage("#GITLAB-1,#GITLAB-2")).thenReturn(Arrays.asList("GITLAB-1", "GITLAB-2"));

        long userId = 1L;
        String repositoryName = "test-repo";
        Event event = Event.builder()
                .type(Event.Type.PUSH)
                .userId(userId)
                .repository(Repository.builder().name(repositoryName).build())
                .commits(Arrays.asList(commit1, commit2))
                .build();

        service.performPushEvent(event);

        verify(service, times(1)).commentIssue(eq(repositoryName), any(User.class), eq(Arrays.asList(commit1, commit2)), eq("GITLAB-1"));
        verify(service, times(1)).commentIssue(eq(repositoryName), any(User.class), eq(Collections.singletonList(commit2)), eq("GITLAB-2"));
    }

    @Test
    public void performPushEventWithoutMentionedIssuesShouldNotCallGetUserMethod() throws IOException {
        service = spy(service);
        String repositoryName = "test-repo";

        long userId = 1L;
        Event event = Event.builder()
                .type(Event.Type.PUSH)
                .userId(userId)
                .repository(Repository.builder().name(repositoryName).build())
                .commits(Collections.emptyList())
                .build();

        service.performPushEvent(event);

        verify(service, times(0)).getUser(event);
    }

    @Test
    public void getUserWhenGitLabServiceIsAvailable() throws Exception {
        Long userId = 10L;
        User user = new User(userId, "John Smith", "john.smith@mocked.com");
        when(gitLabService.getUser(userId)).thenReturn(Response.success(user));

        assertThat(service.getUser(Event.builder().userId(userId).build())).isEqualTo(user);

        verify(gitLabService, times(1)).getUser(userId);
    }
}