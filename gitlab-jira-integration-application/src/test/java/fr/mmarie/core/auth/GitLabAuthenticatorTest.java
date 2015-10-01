package fr.mmarie.core.auth;

import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;

import java.security.Principal;

import static org.assertj.core.api.Assertions.assertThat;

public class GitLabAuthenticatorTest {

    private GitLabAuthenticator gitLabAuthenticator;

    private final String password = "test-password";

    @Before
    public void setUp() throws Exception {
        gitLabAuthenticator = new GitLabAuthenticator(password);
    }

    @Test
    public void authenticateWithBadCredentialsShouldReturnAnAbsentPrincipal() throws Exception {
        Optional<Principal> principalOptional = gitLabAuthenticator.authenticate(
                new GitLabCredentials("bad-svc", "bad-pwd"));

        assertThat(principalOptional.isPresent()).isFalse();
    }

    @Test
    public void authenticateWithGoodCredentialsShouldReturnAPrincipal() throws Exception {
        Optional<Principal> principalOptional = gitLabAuthenticator.authenticate(
                new GitLabCredentials("good-svc", password));

        assertThat(principalOptional.isPresent()).isTrue();
        assertThat(principalOptional.get().getName()).isEqualTo("good-svc");
    }
}