package fr.mmarie.core.jira;

import com.google.common.collect.ImmutableList;
import fr.mmarie.api.jira.Comment;
import fr.mmarie.api.jira.Transition;
import fr.mmarie.api.jira.input.TransitionInput;
import fr.mmarie.api.jira.response.TransitionResponse;
import org.assertj.core.data.MapEntry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JiraServiceTest {

    public static final int PORT = 1520;

    public JiraConfiguration jiraConfiguration = new JiraConfiguration("username",
            "password",
            String.format("http://localhost:%d", PORT),
            ImmutableList.of(new TransitionConfiguration("Close", ImmutableList.of("close", "fix")),
                    new TransitionConfiguration("Start Progress", ImmutableList.of("starts", "starting"))));

    public JiraService jiraService;

    @Before
    public void setUp() throws Exception {
        jiraService = new JiraService(jiraConfiguration);
    }

    @Test
    public void extractIssuesFromMessageWithoutIssue() throws Exception {
        String message = "test: no issue";

        final List<String> issues = jiraService.extractIssuesFromMessage(message);

        assertThat(issues)
                .hasSize(0);
    }

    @Test
    public void extractIssuesFromMessageWithOneIssue() throws Exception {
        String message = "test(#TEST-1): single issue";

        final List<String> issues = jiraService.extractIssuesFromMessage(message);

        assertThat(issues)
                .hasSize(1)
                .containsExactly("TEST-1");
    }

    @Test
    public void extractIssuesFromMessageWithMoreThanOneIssue() throws Exception {
        String message = "test(#TEST-1): issue related to #TEST-15289";

        final List<String> issues = jiraService.extractIssuesFromMessage(message);

        assertThat(issues)
                .hasSize(2)
                .containsExactly("TEST-1", "TEST-15289");
    }

    @Test
    public void extractMatchingTransitionsFromMessageWithoutTransition() throws Exception {
        String message = "test: no issue";

        final Map<String, String> matchingTransitions = jiraService.extractMatchingTransitionsFromMessage(message);

        assertThat(matchingTransitions)
                .isEmpty();
    }

    @Test
    public void extractMatchingTransitionsFromMessageWithOneTransition() throws Exception {
        String message = "test: close #TEST-15289";

        final Map<String, String> matchingTransitions = jiraService.extractMatchingTransitionsFromMessage(message);

        assertThat(matchingTransitions)
                .hasSize(1);
        assertThat(matchingTransitions)
                .containsOnly(MapEntry.entry("TEST-15289", "Close"));
    }

    @Test
    public void extractMatchingTransitionsFromMessageCaseInsensitive() throws Exception {
        String message = "test: FIX #TEST-15289";

        final Map<String, String> matchingTransitions = jiraService.extractMatchingTransitionsFromMessage(message);

        assertThat(matchingTransitions)
                .hasSize(1);
        assertThat(matchingTransitions)
                .containsOnly(MapEntry.entry("TEST-15289", "Close"));
    }

    @Test
    public void extractMatchingTransitionsFromMessageWithTwoTransitionResturnTheFirstOne() throws Exception {
        String message = "test: Close #TEST-15289 and FIX #TEST-52";

        final Map<String, String> matchingTransitions = jiraService.extractMatchingTransitionsFromMessage(message);

        assertThat(matchingTransitions)
                .hasSize(2);
        assertThat(matchingTransitions)
                .containsOnly(MapEntry.entry("TEST-15289", "Close"), MapEntry.entry("TEST-52", "Close"));
    }

    @Test
    public void performTransitionWithoutKeyword() throws Exception {
        jiraService = spy(jiraService);

        String message = "dummy";
        String issue = "TESGITLAB-1";

        doReturn(Optional.empty()).when(jiraService).extractMatchingTransitionsFromMessage(message, issue);

        jiraService.performTransition(message, issue, "Hello");

        verify(jiraService, times(1)).extractMatchingTransitionsFromMessage(message, issue);
        verify(jiraService, times(0)).transitionOnIssue(eq(issue), any(TransitionInput.class));
    }

    @Test
    public void performTransitionWithAnUnknownTransition() throws Exception {
        jiraService = spy(jiraService);

        String issue = "TESGITLAB-1";
        String message = "dummy closes #" + issue;

        String transitionName = "close";
        doReturn(Optional.of(transitionName)).when(jiraService).extractMatchingTransitionsFromMessage(message, issue);
        doReturn(Optional.empty()).when(jiraService).getTransition(issue, transitionName);

        jiraService.performTransition(message, issue, "Hello");

        verify(jiraService, times(1)).extractMatchingTransitionsFromMessage(message, issue);
        verify(jiraService, times(0)).transitionOnIssue(eq(issue), any(TransitionInput.class));
    }

    @Test
    public void performTransitionWithARightTransition() throws Exception {
        jiraService = spy(jiraService);

        ArgumentCaptor<TransitionInput> transitionInputArgumentCaptor = ArgumentCaptor.forClass(TransitionInput.class);

        String issue = "TESGITLAB-1";
        String message = "dummy closes #" + issue;

        String transitionName = "close";
        doReturn(Optional.of(transitionName)).when(jiraService).extractMatchingTransitionsFromMessage(message, issue);
        Transition transition = new Transition(15L, "Close");
        doReturn(Optional.of(transition)).when(jiraService).getTransition(issue, transitionName);

        jiraService.performTransition(message, issue, "Hello " + JiraService.TRANSITION_HOLDER);

        verify(jiraService, times(1)).extractMatchingTransitionsFromMessage(message, issue);
        verify(jiraService, times(1)).transitionOnIssue(eq(issue), transitionInputArgumentCaptor.capture());

        TransitionInput transitionInput = transitionInputArgumentCaptor.getValue();

        assertThat(transitionInput.getTransition()).isEqualTo(transition);
        assertThat(transitionInput.getUpdate().getComments().size()).isEqualTo(1);

        assertThat(transitionInput.getUpdate().getComments().get(0).getComment())
                .isEqualTo(new Comment("Hello " + transitionName));
    }

    @Test
    public void performTransitionWithMultipleTransitions() throws Exception {
        jiraService = spy(jiraService);

        ArgumentCaptor<TransitionInput> transitionInputArgumentCaptor = ArgumentCaptor.forClass(TransitionInput.class);

        String issue = "TESGITLAB-1";
        Transition startProgressTransition = new Transition(2L, "Start Progress");
        Transition closeTransition = new Transition(1L, "Close");
        doReturn(new TransitionResponse(ImmutableList.of(closeTransition, startProgressTransition)))
                .when(jiraService).getTransitionsOfIssue(issue);
        String message = "dummy starts #" + issue;

        String transitionName = "Start Progress";
        doReturn(Optional.of(transitionName)).when(jiraService).extractMatchingTransitionsFromMessage(message, issue);

        jiraService.performTransition(message, issue, "Hello " + JiraService.TRANSITION_HOLDER);

        verify(jiraService, times(1)).extractMatchingTransitionsFromMessage(message, issue);
        verify(jiraService, times(1)).transitionOnIssue(eq(issue), transitionInputArgumentCaptor.capture());

        TransitionInput transitionInput = transitionInputArgumentCaptor.getValue();

        assertThat(transitionInput.getTransition()).isEqualTo(startProgressTransition);
        assertThat(transitionInput.getUpdate().getComments().size()).isEqualTo(1);

        assertThat(transitionInput.getUpdate().getComments().get(0).getComment())
                .isEqualTo(new Comment("Hello " + transitionName));
    }
}
