package fr.mmarie.core.gitlab;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GitLabServiceTest {

    private GitLabService gitLabService;

    @Before
    public void setUp() throws Exception {
        gitLabService = new GitLabService();
    }

    @Test
    public void extractIssuesFromMessageWithoutIssue() throws Exception {
        String message = "test: no issue";

        final List<String> issues = gitLabService.extractIssuesFromMessage(message);

        assertThat(issues)
                .hasSize(0);
    }

    @Test
    public void extractIssuesFromMessageWithOneIssue() throws Exception {
        String message = "test(#TEST-1): single issue";

        final List<String> issues = gitLabService.extractIssuesFromMessage(message);

        assertThat(issues)
                .hasSize(1)
                .containsExactly("TEST-1");
    }

    @Test
    public void extractIssuesFromMessageWithMoreThanOneIssue() throws Exception {
        String message = "test(#TEST-1): issue related to #TEST-15289";

        final List<String> issues = gitLabService.extractIssuesFromMessage(message);

        assertThat(issues)
                .hasSize(2)
                .containsExactly("TEST-1", "TEST-15289");
    }
}